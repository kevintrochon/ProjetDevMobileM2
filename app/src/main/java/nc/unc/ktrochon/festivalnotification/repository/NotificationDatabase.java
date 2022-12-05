package nc.unc.ktrochon.festivalnotification.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;

/**
 * Classe de configuration pour l ORM ROOM.
 */
@Database(entities = {FavoriConcert.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {
    public abstract FavoriDAO favoriDAO();
}
