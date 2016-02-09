package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerBucketEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerBucketInfo;

public class fPlayerBucket extends fPlayer {
	private PlayerBucketInfo bucketInfo;
	
	public fPlayerBucket(PlayerBucketEvent event) {
		super(event, FlyweightType.Bucket);
		
		if(event != null) {
			this.bucketInfo = new PlayerBucketInfo();
			this.bucketInfo.trace_id = this.eventInfo.trace_id;
			this.bucketInfo.item = new ItemInfo(event.getItemStack());
			this.bucketInfo.clickedBlock = event.getBlockClicked() != null ? event.getBlockClicked().getType().name(): null;
			this.bucketInfo.blockFace = event.getBlockFace() != null ? event.getBlockFace().name(): null;
			this.bucketInfo.bucket = event.getBucket() != null ? event.getBucket().name(): null;
			this.bucketInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		// in context of file IO it isn't necessary to write the unique UUID twice b/c the parent
		// and child records are written together.
		marshallItemToStream(this.bucketInfo.item, os);
		os.writeUTF(this.bucketInfo.clickedBlock != null ? this.bucketInfo.clickedBlock: "");
		os.writeUTF(this.bucketInfo.blockFace != null ? this.bucketInfo.blockFace: "");
		os.writeUTF(this.bucketInfo.bucket != null ? this.bucketInfo.bucket: "");
		os.writeBoolean(this.bucketInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.bucketInfo = new PlayerBucketInfo();
		this.bucketInfo.trace_id = this.eventInfo.trace_id;
		this.bucketInfo.item = unmarshallItemFromStream(is);
		
		this.bucketInfo.clickedBlock = is.readUTF();
		if(this.bucketInfo.clickedBlock == "") this.bucketInfo.clickedBlock = null;
		
		this.bucketInfo.blockFace = is.readUTF();
		if(this.bucketInfo.blockFace == "") this.bucketInfo.blockFace = null;
		
		this.bucketInfo.bucket = is.readUTF();
		if(this.bucketInfo.bucket == "") this.bucketInfo.bucket = null;

		this.bucketInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerBucketSource().insert(this.bucketInfo);
	}
}
