package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerStatisticIncrementInfo;

public class fPlayerStatisticIncrement extends fPlayer {
	private PlayerStatisticIncrementInfo statisticIncrementInfo;
	
	public fPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
		super(event, FlyweightType.StatisticIncrement);
		
		if(event != null) {
			this.statisticIncrementInfo = new PlayerStatisticIncrementInfo();
			this.statisticIncrementInfo.trace_id = this.eventInfo.trace_id;
			this.statisticIncrementInfo.statistic = event.getStatistic() != null ? event.getStatistic().name(): null;
			this.statisticIncrementInfo.prevValue = event.getPreviousValue();
			this.statisticIncrementInfo.newValue = event.getNewValue();
			this.statisticIncrementInfo.entityType = event.getEntityType() != null ? event.getEntityType().name(): null;
			this.statisticIncrementInfo.material = event.getMaterial() != null ? event.getMaterial().name(): null;
			this.statisticIncrementInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.statisticIncrementInfo.statistic != null ? this.statisticIncrementInfo.statistic: "");
		os.writeInt(this.statisticIncrementInfo.prevValue);
		os.writeInt(this.statisticIncrementInfo.newValue);
		os.writeUTF(this.statisticIncrementInfo.entityType != null ? this.statisticIncrementInfo.entityType: "");
		os.writeUTF(this.statisticIncrementInfo.material != null ? this.statisticIncrementInfo.material: "");
		os.writeBoolean(this.statisticIncrementInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.statisticIncrementInfo = new PlayerStatisticIncrementInfo();
		this.statisticIncrementInfo.trace_id = this.eventInfo.trace_id;
		
		this.statisticIncrementInfo.statistic = is.readUTF();
		if(this.statisticIncrementInfo.statistic == "") this.statisticIncrementInfo.statistic = null;
		
		this.statisticIncrementInfo.prevValue = is.readInt();
		this.statisticIncrementInfo.newValue = is.readInt();

		this.statisticIncrementInfo.entityType = is.readUTF();
		if(this.statisticIncrementInfo.entityType == "") this.statisticIncrementInfo.entityType = null;
		
		this.statisticIncrementInfo.material = is.readUTF();
		if(this.statisticIncrementInfo.material == "") this.statisticIncrementInfo.material = null;

		this.statisticIncrementInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerStatisticIncrementSource().insert(this.statisticIncrementInfo);
	}
}
