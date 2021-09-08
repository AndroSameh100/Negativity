package com.elikill58.negativity.universal.alerts.hook;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.alerts.AlertSender;

public class TimeAlertSender implements AlertSender {

	private ScheduledTask task;
	private int time = 1000;
	
	@Override
	public String getName() {
		return "time";
	}
	
	@Override
	public boolean canChangeDefaultValue() {
		return true;
	}
	
	@Override
	public int getDefaultValue() {
		return 1000;
	}
	
	@Override
	public int addOne() {
		return time += 1000;
	}
	
	@Override
	public int removeOne() {
		return time -= 1000;
	}
	
	@Override
	public int getValue() {
		return time;
	}
	
	@Override
	public String getShowedValue() {
		return (int) (getValue() / 1000) + "s";
	}
	
	@Override
	public void stop() {
		if(task != null)
			task.cancel();
	}
	
	@Override
	public void config(Configuration config) {
		time = config.getInt("value", getDefaultValue());
		task = Adapter.getAdapter().getScheduler().runRepeating(() -> {
			NegativityPlayer.getAllPlayers().forEach((uuid, np) -> {
				for(PlayerCheatAlertEvent alert : new ArrayList<>(np.getAlertForAllCheat()))
					Negativity.sendAlertMessage(np, alert);
			});
		}, (time * 20) / 1000);
	}
	
	@Override
	public void alert(NegativityPlayer np, PlayerCheatAlertEvent alert) {
		String cheatKey = alert.getCheat().getKey();
		List<PlayerCheatAlertEvent> tempList = np.ALERT_NOT_SHOWED.computeIfAbsent(cheatKey, (a) -> new ArrayList<>());
		tempList.add(alert);
		np.ALERT_NOT_SHOWED.put(cheatKey, tempList);
	}

}
