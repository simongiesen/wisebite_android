package dev.wisebite.wisebite.utils;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public abstract class FirebaseRepository<T extends Entity> extends Repository<T> {

    private OnChangedListener listener;

    public static final String FIREBASE_URI = "https://wisebite-f7a53.firebaseio.com/";
    private static final String TAG = FirebaseRepository.class.getSimpleName();
    private final HashMap<String, T> map;
    protected Firebase firebase;

    /**
     * Constructor class
     * @param context Repository's context
     */
    public FirebaseRepository(Context context) {
        Firebase.setAndroidContext(context);
        firebase = new Firebase(FIREBASE_URI).child(getObjectReference());
        map = new LinkedHashMap<>();

        firebase.orderByKey().addChildEventListener(this);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    insertInternal(convert(child));
                }
                notifyChange(OnChangedListener.EventType.Full);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Insert some object in specific repository with random id.
     * @param item Object that you want to insert.
     * @return Inserted item
     */
    @Override
    public T insert(T item) {
        Firebase ref = firebase.push();
        ref.setValue(item);
        item.setId(ref.getKey());
        map.put(ref.getKey(), item);
        return item;
    }

    /**
     * Insert some object in specific repository with specific id.
     * @param t   Object that you want to insert.
     * @param key Key that new object will have.
     * @return Inserted item
     */
    private T insertWithId(T t, String key) {
        t.setId(key);
        firebase.child(key).setValue(t);
        map.put(key, t);
        return t;
    }

    /**
     * Delete some object of this repository by id.
     * @param id Object key that you want to delete.
     */
    public void delete(String id) {
        firebase.child(id).removeValue();
        map.remove(id);
    }

    /**
     * Update specific item of repository
     * @param item item that you want to update
     * @return Inserted item
     */
    public T update(T item) {
        delete(item.getId());
        return insertWithId(item, item.getId());
    }

    /**
     * Get object reference URI in Firebase.
     * @return Respective reference URI of object.
     */
    public abstract String getObjectReference();

    /**
     * Get specific item of this repository
     * @param id key that identifies the item
     * @return Item that you want to get
     */
    public T get(String id) {
        return map.get(id);
    }

    /**
     * Get if an specific item exists
     * @param id key that identifies the item
     * @return Item that you want to get
     */
    public boolean exists(String id){
        return map.keySet().contains(id);
    }

    /**
     * Get all repository
     * @return a list that contains all values of this repository
     */
    public List<T> all() {
        return new ArrayList<>(map.values());
    }

    /**
     * Listener that controls when it is occurred an error
     * @param firebaseError error
     */
    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e(TAG, firebaseError.getMessage(), firebaseError.toException());
    }

    /**
     * Insert some object internally
     * @param item Object that you want to insert
     * @return Inserted item
     */
    @Override
    public T insertInternal(T item) {
        map.put(item.getId(), item);
        return item;
    }

    /**
     * Update some object internally
     * @param item Object that you want to update
     * @return Updated item
     */
    @Override
    public T updateInternal(T item) {
        return insertInternal(item);
    }

    /**
     * Delete some object internally
     * @param id Object that you want to delete
     */
    @Override
    public void deleteInternal(String id) {
        map.remove(id);
    }

    /**
     * Convert to object model
     * @param data Object that you want to convert
     * @return Modeled item
     */
    @Override
    protected T convert(DataSnapshot data) {
        return null;
    }

}
