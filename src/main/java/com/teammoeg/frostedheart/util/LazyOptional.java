/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.frostedheart.util;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullPredicate;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * An modified version for LazyOptional by forge, compatibilities for null return
 */
public class LazyOptional<T> {
    private final Supplier<T> supplier;
    private final Object lock = new Object();
    private Mutable<T> resolved;
    private Set<NonNullConsumer<LazyOptional<T>>> listeners = new HashSet<>();
    private boolean isValid = true;
    private static final @Nonnull
    LazyOptional<Void> EMPTY = new LazyOptional<>(null);

    /**
     * Construct a new {@link LazyOptional} that wraps the given
     * {@link NonNullSupplier}.
     *
     * @param instanceSupplier The {@link NonNullSupplier} to wrap. Cannot return
     *                         null, but can be null itself. If null, this method
     *                         returns {@link #empty()}.
     */
    public static <T> LazyOptional<T> of(final @Nullable Supplier<T> instanceSupplier) {
        return instanceSupplier == null ? empty() : new LazyOptional<>(instanceSupplier);
    }

    /**
     * @return The singleton empty instance
     */
    public static <T> LazyOptional<T> empty() {
        return EMPTY.cast();
    }

    /**
     * This method hides an unchecked cast to the inferred type. Only use this if
     * you are sure the type should match. For capabilities, generally
     * {@link Capability#orEmpty(Capability, LazyOptional)} should be used.
     *
     * @return This {@link LazyOptional}, cast to the inferred generic type
     */
    @SuppressWarnings("unchecked")
    public <X> LazyOptional<X> cast() {
        return (LazyOptional<X>) this;
    }

    private LazyOptional(@Nullable Supplier<T> instanceSupplier) {
        this.supplier = instanceSupplier;
    }

    private @Nullable
    T getValue() {
        if (!isValid || supplier == null)
            return null;
        if (resolved == null) {
            synchronized (lock) {
                // resolved == null: Double checked locking to prevent two threads from resolving
                if (resolved == null) {
                    T temp = supplier.get();
                    if (temp == null) {
                        return null;
                    }
                    resolved = new MutableObject<>(temp);
                    this.listeners.forEach(e -> e.accept(this));
                }
            }
        }
        return resolved.getValue();
    }

    /**
     * Check if this {@link LazyOptional} is non-empty.
     *
     * @return {@code true} if this {@link LazyOptional} is non-empty, i.e. holds a
     * non-null supplier
     */
    public boolean isPresent() {
        if (supplier == null || !isValid) return false;
        return getValue() != null;
    }

    /**
     * If non-empty, invoke the specified {@link NonNullConsumer} with the object,
     * otherwise do nothing.
     *
     * @param consumer The {@link NonNullConsumer} to run if this optional is non-empty.
     * @throws NullPointerException if {@code consumer} is null and this {@link LazyOptional} is non-empty
     */
    public void ifPresent(NonNullConsumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        T val = getValue();
        if (isValid && val != null)
            consumer.accept(val);
    }

    /**
     * If a this {@link LazyOptional} is non-empty, return a new
     * {@link LazyOptional} encapsulating the mapping function. Otherwise, returns
     * {@link #empty()}.
     * <p>
     * The supplier inside this object is <strong>NOT</strong> resolved.
     *
     * @param mapper A mapping function to apply to the mod object, if present
     * @return A {@link LazyOptional} describing the result of applying a mapping
     * function to the value of this {@link LazyOptional}, if a value is
     * present, otherwise an empty {@link LazyOptional}
     * @throws NullPointerException if {@code mapper} is null.
     * @apiNote This method supports post-processing on optional values, without the
     * need to explicitly check for a return status.
     * @apiNote The returned value does not receive invalidation messages from the original {@link LazyOptional}.
     * If you need the invalidation, you will need to manage them yourself.
     */
    public <U> LazyOptional<U> lazyMap(NonNullFunction<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() ? of(() -> mapper.apply(getValue())) : empty();
    }

    /**
     * If a this {@link LazyOptional} is non-empty, return a new
     * {@link Optional} encapsulating the mapped value. Otherwise, returns
     * {@link Optional#empty()}.
     *
     * @param mapper A mapping function to apply to the mod object, if present
     * @return An {@link Optional} describing the result of applying a mapping
     * function to the value of this {@link Optional}, if a value is
     * present, otherwise an empty {@link Optional}
     * @throws NullPointerException if {@code mapper} is null.
     * @apiNote This method explicitly resolves the value of the {@link LazyOptional}.
     * For a non-resolving mapper that will lazily run the mapping, use {@link #lazyMap(NonNullFunction)}.
     */
    public <U> Optional<U> map(NonNullFunction<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() ? Optional.of(mapper.apply(getValue())) : Optional.empty();
    }

    /**
     * Resolve the contained supplier if non-empty, and filter it by the given
     * {@link NonNullPredicate}, returning empty if false.
     * <p>
     * <em>It is important to note that this method is <strong>not</strong> lazy, as
     * it must resolve the value of the supplier to validate it with the
     * predicate.</em>
     *
     * @param predicate A {@link NonNullPredicate} to apply to the result of the
     *                  contained supplier, if non-empty
     * @return An {@link Optional} containing the result of the contained
     * supplier, if and only if the passed {@link NonNullPredicate} returns
     * true, otherwise an empty {@link Optional}
     * @throws NullPointerException If {@code predicate} is null and this
     *                              {@link Optional} is non-empty
     */
    public Optional<T> filter(NonNullPredicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        final T value = getValue(); // To keep the non-null contract we have to evaluate right now. Should we allow this function at all?
        return value != null && predicate.test(value) ? Optional.of(value) : Optional.empty();
    }

    /**
     * Resolves the value of this LazyOptional, turning it into a standard non-lazy {@link Optional<T>}
     *
     * @return The resolved optional.
     */
    public Optional<T> resolve() {
        return isPresent() ? Optional.ofNullable(getValue()) : Optional.empty();
    }

    /**
     * Resolve the contained supplier if non-empty and return the result, otherwise return
     * {@code other}.
     *
     * @param other the value to be returned if this {@link LazyOptional} is empty
     * @return the result of the supplier, if non-empty, otherwise {@code other}
     */
    public T orElse(T other) {
        T val = getValue();
        return val != null ? val : other;
    }

    /**
     * Resolve the contained supplier if non-empty and return the result, otherwise return the
     * result of {@code other}.
     *
     * @param other A {@link NonNullSupplier} whose result is returned if this
     *              {@link LazyOptional} is empty
     * @return The result of the supplier, if non-empty, otherwise the result of
     * {@code other.get()}
     * @throws NullPointerException If {@code other} is null and this
     *                              {@link LazyOptional} is non-empty
     */
    public T orElseGet(Supplier<? extends T> other) {
        T val = getValue();
        return val != null ? val : other.get();
    }

    /**
     * Resolve the contained supplier if non-empty and return the result, otherwise throw the
     * exception created by the provided {@link NonNullSupplier}.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The {@link NonNullSupplier} which will return the
     *                          exception to be thrown
     * @return The result of the supplier
     * @throws X                    If this {@link LazyOptional} is empty
     * @throws NullPointerException If {@code exceptionSupplier} is null and this
     *                              {@link LazyOptional} is empty
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        T val = getValue();
        if (val != null)
            return val;
        throw exceptionSupplier.get();
    }

    /**
     * Register a {@link NonNullConsumer listener} that will be called when this {@link LazyOptional} becomes resolved
     */
    public void addListener(NonNullConsumer<LazyOptional<T>> listener) {
        if (!isPresent()) {
            this.listeners.add(listener);
        } else {
            listener.accept(this);
        }
    }
}