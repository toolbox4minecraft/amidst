package amidst.gui.main.bookmarks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import java.util.function.Consumer;
import java.util.function.Function;

import ca.odell.glazedlists.EventList;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.event.ListEventPublisher;

import ca.odell.glazedlists.util.concurrent.ReadWriteLock;

class ForwardingEventList<E> implements EventList<E> {
    private final EventList<E> eventList;

    protected ForwardingEventList(final EventList<E> eventList) {
        if (eventList == null) {
            throw new NullPointerException("eventList must not be null");
        }
        this.eventList = eventList;
    }

    @Override
    public void addListEventListener(ListEventListener<? super E> listChangeListener) {
        eventList.addListEventListener(listChangeListener);
    }

    @Override
    public void removeListEventListener(ListEventListener<? super E> listChangeListener) {
        eventList.removeListEventListener(listChangeListener);
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return eventList.getReadWriteLock();
    }

    @Override
    public ListEventPublisher getPublisher() {
        return eventList.getPublisher();
    }

    @Override
    public void dispose() {
        eventList.dispose();
    }

    @Override
    public Iterator<E> iterator() {
        return eventList.iterator();
    }

    @Override
    public int size() {
        return eventList.size();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return eventList.removeAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return eventList.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        return eventList.contains(object);
    }

    @Override
    public boolean add(E element) {
        return eventList.add(element);
    }

    @Override
    public boolean remove(Object object) {
        return eventList.remove(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return eventList.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return eventList.addAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return eventList.retainAll(collection);
    }

    @Override
    public void clear() {
        eventList.clear();
    }

    @Override
    public Object[] toArray() {
        return eventList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return eventList.toArray(array);
    }

    @Override
    public void add(int index, E element) {
        eventList.add(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> elements) {
        return eventList.addAll(index, elements);
    }

    @Override
    public E get(int index) {
        return eventList.get(index);
    }

    @Override
    public int indexOf(Object element) {
        return eventList.indexOf(element);
    }

    @Override
    public int lastIndexOf(Object element) {
        return eventList.lastIndexOf(element);
    }

    @Override
    public ListIterator<E> listIterator() {
        return eventList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return eventList.listIterator(index);
    }

    @Override
    public E remove(int index) {
        return eventList.remove(index);
    }

    @Override
    public E set(int index, E element) {
        return eventList.set(index, element);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return eventList.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object object) {
        return object == this || eventList.equals(object);
    }

    @Override
    public int hashCode() {
        return eventList.hashCode();
    }
}
