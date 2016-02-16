package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerEditBookInfo;

public class fPlayerEditBook extends fPlayer {
	private PlayerEditBookInfo editBookInfo;
	
	public fPlayerEditBook(PlayerEditBookEvent event) {
		super(event, FlyweightType.EditBook);
		
		if(event != null) {
			this.editBookInfo = new PlayerEditBookInfo();
			this.editBookInfo.trace_id = this.eventInfo.trace_id;
			this.editBookInfo.slot = event.getSlot();
			this.editBookInfo.signing = event.isSigning();
			
			BookMeta prevBookMeta = event.getPreviousBookMeta();
			BookMeta newBookMeta = event.getNewBookMeta();
			
			//TODO should record titles as well for edit tracking
			
			if(prevBookMeta != null && newBookMeta != null) {
				this.editBookInfo.titleChanged = prevBookMeta.hasTitle() != newBookMeta.hasTitle()
						|| prevBookMeta.hasTitle() && !prevBookMeta.getTitle().equals(newBookMeta.getTitle());
				
				this.editBookInfo.authorChanged = prevBookMeta.hasAuthor() != newBookMeta.hasAuthor()
						|| prevBookMeta.hasAuthor() && !prevBookMeta.getAuthor().equals(newBookMeta.getAuthor());

				this.editBookInfo.pageCountChanged = prevBookMeta.hasPages() != newBookMeta.hasPages()
						|| prevBookMeta.hasPages() && prevBookMeta.getPageCount() != newBookMeta.getPageCount();
				
				this.editBookInfo.contentChanged = this.editBookInfo.pageCountChanged
						|| prevBookMeta.hasPages() && CheckContentChanged(prevBookMeta, newBookMeta);
			}
			
			this.editBookInfo.eventCancelled = event.isCancelled();
		}
	}
	
	private static final Boolean CheckContentChanged(BookMeta prevBookMeta, BookMeta newBookMeta) {
		List<String> prevPages = prevBookMeta.getPages();
		List<String> newPages = newBookMeta.getPages();
		
		for(int i = 0; i < prevPages.size(); i++) {
			if(!prevPages.get(i).equals(newPages.get(i))) return true;
		}
		
		return false;
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeInt(this.editBookInfo.slot);
		os.writeBoolean(this.editBookInfo.signing);
		os.writeBoolean(this.editBookInfo.titleChanged);
		os.writeBoolean(this.editBookInfo.authorChanged);
		os.writeBoolean(this.editBookInfo.contentChanged);
		os.writeBoolean(this.editBookInfo.pageCountChanged);
		os.writeBoolean(this.editBookInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.editBookInfo = new PlayerEditBookInfo();
		this.editBookInfo.trace_id = this.eventInfo.trace_id;
		this.editBookInfo.slot = is.readInt();
		this.editBookInfo.signing = is.readBoolean();
		this.editBookInfo.titleChanged = is.readBoolean();
		this.editBookInfo.authorChanged = is.readBoolean();
		this.editBookInfo.contentChanged = is.readBoolean();
		this.editBookInfo.pageCountChanged = is.readBoolean();
		this.editBookInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerEditBookSource().insert(this.editBookInfo);
	}
}