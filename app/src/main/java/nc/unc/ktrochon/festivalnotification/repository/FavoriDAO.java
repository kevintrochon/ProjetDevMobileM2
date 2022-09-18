package nc.unc.ktrochon.festivalnotification.repository;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;

@Dao
public interface FavoriDAO {
    @Query("SELECT * FROM FavoriConcert")
    List<FavoriConcert> loadAll();

    @Query("SELECT * FROM FavoriConcert WHERE favoriId = :id")
    FavoriConcert loadById(int id);

    @Insert
    void insertFavori(FavoriConcert favoriConcert);

    @Delete
    void deleteFavori(FavoriConcert favoriConcert);
}
