package com.teammoeg.frostedheart.content.scenario.commands;

import java.util.HashMap;
import java.util.Map;

import com.teammoeg.frostedheart.content.scenario.FHScenario;
import com.teammoeg.frostedheart.content.scenario.Param;
import com.teammoeg.frostedheart.content.scenario.runner.RunStatus;
import com.teammoeg.frostedheart.content.scenario.runner.ScenarioCommandContext;

public class TextualCommands {
	public void nowait(ScenarioCommandContext runner) {
		runner.thread().scene().sendCached(runner.context());
		runner.thread().scene().isNowait=true;
	}
	public void endnowait(ScenarioCommandContext runner) {
		runner.thread().scene().sendCached(runner.context());
		runner.thread().scene().isNowait=false;
	}
	public void r(ScenarioCommandContext runner) {
		runner.thread().scene().sendNewLine(runner.context(), RunStatus.RUNNING, false);
	}
	public void link(ScenarioCommandContext runner,@Param("lid")String linkId,@Param("s")String scenario,@Param("l")String label) {
		runner.thread().scene().sendCached(runner.context());
		linkId=runner.thread().scene().createLink(linkId, scenario, label);
		Map<String,String> pars=new HashMap<>();
		pars.put("lid", linkId);
		FHScenario.callClientCommand("link", runner.context(), pars);
	}
	public void endlink(ScenarioCommandContext runner) {
		runner.thread().scene().sendCached(runner.context());
		Map<String,String> pars=new HashMap<>();
		FHScenario.callClientCommand("endlink", runner.context(), pars);
	}
	public void nolink(ScenarioCommandContext runner) {
		runner.thread().scene().clearLink();
	}
	public void delay(ScenarioCommandContext runner,@Param("t")int t) {
		runner.thread().addWait(t);
		runner.thread().scene().sendCurrent(runner.context(), RunStatus.WAITTIMER,false);
	}
	public void er(ScenarioCommandContext runner) {
		runner.thread().scene().clear(runner.context(),RunStatus.RUNNING);
	}
	public void l(ScenarioCommandContext runner) {
		runner.thread().waitClient();
		runner.thread().scene().sendCurrent(runner.context(), RunStatus.WAITCLIENT,false);
	}
	public void wc(ScenarioCommandContext runner) {
		runner.thread().waitClient();
		runner.thread().scene().sendCurrent(runner.context(), RunStatus.WAITCLIENT,true);
	}
	public void wt(ScenarioCommandContext runner) {
		runner.thread().setStatus((RunStatus.WAITTRIGGER));
		runner.thread().scene().sendCurrent(runner.context(), RunStatus.WAITTRIGGER,false);
	}
	public void wa(ScenarioCommandContext runner) {
		runner.thread().setStatus((RunStatus.WAITACTION));
		runner.thread().scene().sendCurrent(runner.context(), RunStatus.WAITACTION,false);
	}
	public void s(ScenarioCommandContext runner) {
		runner.thread().stop();
		runner.thread().scene().sendCurrent(runner.context(), RunStatus.STOPPED,false);
	}

}
