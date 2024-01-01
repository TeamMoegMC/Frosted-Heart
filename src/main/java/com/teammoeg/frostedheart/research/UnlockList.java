/*
 * Copyright (c) 2022-2024 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.teammoeg.frostedheart.research;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public abstract class UnlockList<T> implements Iterable<T> {
    Set<T> s = new HashSet<>();

    public UnlockList() {

    }

    public UnlockList(ListTag nbt) {
        this();
        load(nbt);
    }
    public void clear() {
    	s.clear();
    }
    public boolean has(T key) {
        return s.contains(key);
    }

    public void add(T key) {
        s.add(key);
    }

    public void addAll(Collection<T> key) {
        s.addAll(key);
    }

    public abstract String getString(T item);

    public abstract T getObject(String s);

    public ListTag serialize() {
        ListTag ln = new ListTag();
        for (T t : s)
            ln.add(StringTag.valueOf(getString(t)));
        return ln;
    }

    public void remove(T key) {
        s.remove(key);
    }

    public void removeAll(Collection<T> key) {
        s.removeAll(key);
    }

    public void load(ListTag nbt) {
        for (Tag in : nbt) {
            s.add(getObject(in.getAsString()));
        }
    }

    @Override
    public Iterator<T> iterator() {
        return s.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        s.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return s.spliterator();
    }
    public void reload() {
    	Set<T> ns=new HashSet<>(s);
    	s.clear();
    	ns.stream().map(this::getString).map(this::getObject).forEach(s::add);
    }
}
