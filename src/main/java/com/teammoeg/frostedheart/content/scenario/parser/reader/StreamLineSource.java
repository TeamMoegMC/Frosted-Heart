package com.teammoeg.frostedheart.content.scenario.parser.reader;

public abstract class StreamLineSource implements CodeLineSource {
	String name;
	boolean hasNext=true;
	public StreamLineSource(String name) {
		super();
		this.name = name;
	}

	public abstract int readCh();

	@Override
	public String read() {
		StringBuilder sb=new StringBuilder();
		int ch;
		while((ch=readCh())>0) {
			if(ch=='\r'||ch=='\n')
				break;
			sb.appendCodePoint(ch);
		}
		if(ch<0)hasNext=false;
		return sb.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public final boolean hasNext() {
		return hasNext;
	}

}
