package ru.yandex.practicum.filmorate.storage.dao.mpa;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

public interface MPARateDao {
    MPA getMPAById(int id);

    Collection<MPA> getAllMPA();
}