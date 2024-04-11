/*
 * Copyright (c) 2024 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.teammoeg.frostedheart.content.scenario.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.teammoeg.frostedheart.content.scenario.ScenarioExecutionException;
import com.teammoeg.frostedheart.content.scenario.parser.reader.CodeLineSource;
import com.teammoeg.frostedheart.content.scenario.parser.reader.ReaderLineSource;
import com.teammoeg.frostedheart.content.scenario.parser.reader.StringLineSource;
import com.teammoeg.frostedheart.content.scenario.parser.reader.StringListStringSource;
import com.teammoeg.frostedheart.content.scenario.parser.reader.StringParseReader;

public class ScenarioParser {
    private static class CommandStack {
        IfNode f;
        List<ElseNode> elses=new ArrayList<>();
        public CommandStack( IfNode f) {
            super();
            this.f=f;
        }

        public void add(int idx, ElsifNode elsif) {
            f.elsifs.put(elsif.exp, idx+1);
            elses.add(elsif);
        }

        public void setElse(int idx,ElseNode els) {
            if(f.elseBlock==-1)
            	f.elseBlock=idx+1;
            elses.add(els);
        }

        public void setEndif(int idx) {
        	if(f.elseBlock==-1)
            	f.elseBlock=idx;
        	elses.forEach(t->t.target=idx);
        }
    }

    private Node createCommand(String command, Map<String, String> params) {
        switch (command) {
            case "eval":
                return new AssignNode(command, params);
            case "if":
            	return new IfNode(command, params);
            case "elsif":
            	return new ElsifNode(command, params);
            case "else":
            	return new ElseNode(command, params);
            case "endif":
                return new EndIfNode(command, params);
            case "emb":
                return new EmbNode(command, params);
            case "label":
                return new LabelNode(command, params);
            case "p":
                return new ParagraphNode(command, params);
            case "save":
            	return new SavepointNode(command, params);
            case "sharp":
            	return new ShpNode(command,params);
            case "include":
            	return new IncludeNode(command,params);
        }
        return new CommandNode(command, params);

    }
    public Scenario parseString(String name,List<String> code) {
        return process(name,parseLine(new StringListStringSource(name,code)));
    }
    public Scenario parseString(String name,String code){
        return process(name,parseLine(new StringLineSource(name,code)));
    }
    public Scenario parseFile(String name,File file) {
        try (FileInputStream fis = new FileInputStream(file);InputStreamReader isr = new InputStreamReader(fis,StandardCharsets.UTF_8)) {
        	return process(name,parseLine(new ReaderLineSource(name,isr)));

        }catch(IOException ex) {//ignored exception when closed
        }
        return new Scenario(name);
        
    }
    private Scenario process(String name,List<Node> nodes) {
    	List<Integer> paragraphs = new ArrayList<>();
        LinkedList<CommandStack> ifstack = new LinkedList<>();
        Map<String,Integer> labels=new HashMap<>();
        int macro=0;
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n instanceof ParagraphNode) {
                paragraphs.add(i);
                ((ParagraphNode) n).nodeNum=paragraphs.size();
            }else if(n instanceof LabelNode) {
            	labels.put(((LabelNode)n).name, i);
            }else if (n instanceof IfNode) {
                IfNode ifn = (IfNode) n;
                ifstack.add(new CommandStack(ifn));
            }else if (n instanceof ElsifNode) {
            	ElsifNode ifn = (ElsifNode) n;
            	if(ifstack.isEmpty())
            		throw new ScenarioExecutionException("At file "+name+" Unexpected Elsif!");
                ifstack.peekLast().add(i, ifn);
            } else if (n instanceof ElseNode) {
                ElseNode els = (ElseNode) n;
                if(ifstack.isEmpty())
            		throw new ScenarioExecutionException("At file "+name+" Unexpected Else!");
                ifstack.peekLast().setElse(i,els);
            } else if (n instanceof EndIfNode) {
            	if(ifstack.isEmpty())
            		throw new ScenarioExecutionException("At file "+name+" Unexpected Endif!");
                ifstack.pollLast().setEndif(i);
            }else if(n instanceof CommandNode) {
            	CommandNode cmd=(CommandNode) n;
            	if("macro".equals(cmd.command)){
            		macro++;
            	}
            	if("endmacro".equals(cmd.command)){
            		macro--;
            	}
            }else if(n instanceof IncludeNode) {
            	//IncludeNode in=(IncludeNode) n;
            }
        }
        if(!ifstack.isEmpty()) {
        	throw new ScenarioExecutionException("At file "+name+" could not find endif for if!");
        }
        if(macro!=0) {
        	throw new ScenarioExecutionException("At file "+name+" macro and endmacro not match! "+Math.abs(macro)+" more "+(macro>0?"macro(s)":"endmacro(s)"));
        }
        return new Scenario(name, nodes,paragraphs.stream().mapToInt(t->t).toArray(),labels);
    }
    


    private Node parseAtCommand(StringParseReader reader) {
        Map<String, String> params = new HashMap<>();
        
        String command = parseLiteralOrString(reader, -1);
        reader.skipWhitespace();
        //System.out.println("cmd:"+command);
        if(!reader.has()) return createCommand(command, params);
        while (reader.has()) {
            String name = parseLiteralOrString(reader, '=');
            reader.skipWhitespace();
            if (!reader.eat('=')) {
                 break;
            }
            reader.skipWhitespace();
            
            String val = parseLiteralOrString(reader, -1);
            params.put(name, val);
            reader.skipWhitespace();
            
            if (!reader.has()||reader.eat('#')) return createCommand(command, params);
        }
        return new LiteralNode(reader.fromStart());

    }

    private Node parseBarackCommand(StringParseReader reader) {
        Map<String, String> params = new HashMap<>();
        String command = parseLiteralOrString(reader, ']');
        reader.skipWhitespace();
        
        if(reader.eat(']')) return createCommand(command, params);
        while (reader.has()) {
            String name = parseLiteralOrString(reader, '=');
            reader.skipWhitespace();
            if (!reader.eat('=')) {
                break;
            }
            reader.skipWhitespace();
            String val = parseLiteralOrString(reader, ']');
            params.put(name, val);
            reader.skipWhitespace();
            if(reader.eat(']'))return createCommand(command, params);
        }
        return new LiteralNode(reader.fromStart());
    }

    private List<Node> parseLine(CodeLineSource source) {
        StringParseReader reader = new StringParseReader(source);
        List<Node> nodes = new ArrayList<>();
        while(reader.nextLine()) {
        	try {
		        while (reader.has()) {
		        	reader.saveIndex();
		        	if(reader.eat('#')) {
		        		break;
		        	}else if (reader.eat('@')) {
		                nodes.add(parseAtCommand(reader));
		            } else if (reader.eat('[')) {
		                nodes.add(parseBarackCommand(reader));
		            }else{
		            	String lit=parseLiteral(reader);
		            	if(lit!=null&&!lit.isEmpty()) {
		            		if(lit.startsWith("*")) {
		            			nodes.add(new LabelNode(lit));
		                	}else
		            		nodes.add(new LiteralNode(lit));
		            	}
		            }
		        }
        	}catch(Exception ex) {
        		throw reader.generateException(ex);
        	}
        }
        return nodes;
    }

    private String parseLiteral(StringParseReader reader) {
        StringBuilder all = new StringBuilder();
        boolean isEscaping = false;
        while (reader.has()) {
            char r = reader.read();
            if (!isEscaping && r == '\\') {
                isEscaping = true;
                reader.eat();
                continue;
            }
            if (isEscaping) {
                all.append(r);
                isEscaping = false;
                reader.eat();
                continue;
            }
            if (r == '[' || r == '@'||r=='#') {
                break;
            }
            all.append(r);
            reader.eat();
        }
        return all.toString();
    }

    private String parseLiteralOrString(StringParseReader reader, int ch) {
        StringBuilder all = new StringBuilder();
        boolean isEscaping = false;
        boolean hasQuote = false;
        while (reader.has()) {
            char r = reader.read();
            if (!isEscaping && r == '\\') {
                isEscaping = true;
                reader.eat();
                continue;
            }
            if (isEscaping) {
                all.append(r);
                isEscaping = false;
                reader.eat();
                continue;
            }
            if (r == '"') {
                if (!hasQuote) {
                    hasQuote = true;
                } else {
                	reader.eat();
                    break;
                }
                reader.eat();
                continue;
            }
            if (!hasQuote && (r == ch || Character.isWhitespace(r) || r=='#')) {
                break;
            }
            all.append(r);
            reader.eat();
        }
        return all.toString();
    }
    /*public static void main(String[] args) throws IOException {
    	for(Node n:new ScenarioParser().parseFile("prelogue", new File("config\\fhscenario\\prelogue.ks")).pieces)
    		System.out.println(n.getText());
    }*/
}
