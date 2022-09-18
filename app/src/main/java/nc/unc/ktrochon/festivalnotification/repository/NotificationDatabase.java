package nc.unc.ktrochon.festivalnotification.repository;

import android.arch.persistence.room.Database;

import androidx.room.RoomDatabase;

import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;

@Database(entities = {FavoriConcert.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase{
    public abstract FavoriDAO favoriDAO();
}
