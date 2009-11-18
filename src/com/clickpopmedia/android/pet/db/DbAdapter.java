package com.clickpopmedia.android.pet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.clickpopmedia.android.pet.model.Pet;

/**
 * Retrieves sites from the DB.
 * 
 */
public class DbAdapter {

	public static class Keys {
	    public static final String ID = "_id";
	    public static final String COLOR_INDEX = "color_index";
	    public static final String RECENT_TOY = "recent_toy";
	    public static final String RECENT_FOOD = "recent_food";
	    public static final String TOY_COUNT_BOOK = "toy_count_book";
	    public static final String TOY_COUNT_INSTRUMENT = "toy_count_instrument";
	    public static final String TOY_COUNT_WEIGHTS = "toy_count_weights";
	    public static final String TOY_COUNT_GAME_CONRTOLLER = "toy_count_game_controller";
	    public static final String FOOD_COUNT_MEAT = "food_count_meat";
	    public static final String FOOD_COUNT_SWEETS = "food_count_sweets";
	    public static final String FOOD_COUNT_VEGETABLE = "food_count_vegetable";
	    public static final String SCENERY = "scenery";
	    public static final String STATE = "state";
	}
	
	static final int PET_ID = 1;

	static final String DATABASE_TABLE = "pets";
	
	static final String[] ALL_KEYS = new String[] {
		Keys.ID, 
		Keys.COLOR_INDEX, 
		Keys.RECENT_TOY, 
		Keys.RECENT_FOOD, 
		Keys.TOY_COUNT_BOOK, 
		Keys.TOY_COUNT_INSTRUMENT, 
		Keys.TOY_COUNT_WEIGHTS, 
		Keys.TOY_COUNT_GAME_CONRTOLLER, 
		Keys.FOOD_COUNT_MEAT, 
		Keys.FOOD_COUNT_SWEETS, 
		Keys.FOOD_COUNT_VEGETABLE, 
		Keys.SCENERY, 
		Keys.STATE, 
	};
	
    static final String DATABASE_CREATE =
    	"create table " + DATABASE_TABLE + " (" +
    	"_id integer primary key, " + 
    	"color_index integer not null, " +  
    	"recent_toy integer, " +  
    	"recent_food integer, " +  
    	"toy_count_book integer not null, " +  
    	"toy_count_instrument integer not null, " +  
    	"toy_count_weights integer not null, " +  
    	"toy_count_game_controller integer not null, " +  
    	"food_count_meat integer not null, " +  
    	"food_count_sweets integer not null, " +  
    	"food_count_vegetable integer not null, " + 
    	"scenery integer not null, " +  
    	"state integer not null " + 
    	");";
	
    private DbHelper mDbHelper;
    
    private SQLiteDatabase mDb;
    
    private final Context mContext;

    public DbAdapter(Context context) {
        this.mContext = context;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
	public void save(Pet pet) {
		
        ContentValues initialValues = new ContentValues();       
        initialValues.put(Keys.ID, PET_ID);
       	initialValues.put(Keys.COLOR_INDEX, pet.getColorIndex());
		initialValues.put(Keys.RECENT_TOY, getOrdinal(pet.getRecentToy()));
		initialValues.put(Keys.RECENT_FOOD, getOrdinal(pet.getRecentFood()));
		initialValues.put(Keys.TOY_COUNT_BOOK, pet.getToyCountBook());
		initialValues.put(Keys.TOY_COUNT_INSTRUMENT, pet.getToyCountInstrument());
		initialValues.put(Keys.TOY_COUNT_WEIGHTS, pet.getToyCountWeights());
		initialValues.put(Keys.TOY_COUNT_GAME_CONRTOLLER, pet.getToyCountGameController());
		initialValues.put(Keys.FOOD_COUNT_MEAT, pet.getFoodCountMeat());
		initialValues.put(Keys.FOOD_COUNT_SWEETS, pet.getFoodCountSweets());
		initialValues.put(Keys.FOOD_COUNT_VEGETABLE, pet.getFoodCountVegetable());
		initialValues.put(Keys.SCENERY, getOrdinal(pet.getScenery()));
		initialValues.put(Keys.STATE, getOrdinal(pet.getState()));
		
		mDb.delete(DATABASE_TABLE, null, null);

        mDb.insert(DATABASE_TABLE, null, initialValues);
    }
	
    public Pet load() throws SQLException {

        Cursor cursor = mDb.query(
        	true, DATABASE_TABLE, ALL_KEYS, 
        	Keys.ID + "=" + PET_ID, 
        	null, null, null, null, null);
        
        if ( null == cursor || 0 == cursor.getCount() ) {
        	return new Pet();
        }

        Pet pet = new Pet();
        cursor.moveToFirst();

        pet.setColorIndex(getInt(Keys.COLOR_INDEX, cursor));
        
        pet.setRecentToy(getEnum(Pet.Toy.values(), Keys.RECENT_TOY, cursor));
        pet.setRecentFood(getEnum(Pet.Food.values(), Keys.RECENT_FOOD, cursor));
        pet.setToyCountBook(getInt(Keys.TOY_COUNT_BOOK, cursor));
        pet.setToyCountInstrument(getInt(Keys.TOY_COUNT_INSTRUMENT, cursor));
        pet.setToyCountWeights(getInt(Keys.TOY_COUNT_WEIGHTS, cursor));
        pet.setToyCountGameController(getInt(Keys.TOY_COUNT_GAME_CONRTOLLER, cursor));
        pet.setFoodCountMeat(getInt(Keys.FOOD_COUNT_MEAT, cursor));
        pet.setFoodCountSweets(getInt(Keys.FOOD_COUNT_SWEETS, cursor));
        pet.setFoodCountVegetable(getInt(Keys.FOOD_COUNT_VEGETABLE, cursor));
        pet.setScenery(getEnum(Pet.Scenery.values(), Keys.SCENERY, cursor));
        pet.setState(getEnum(Pet.State.values(), Keys.STATE, cursor));
        
        cursor.close();
        return pet;
    }

    private static <T extends Enum<T>> Integer getOrdinal(Enum<T> e) {
    	if ( null == e ) return null;
    	
    	return e.ordinal();
    }
    
    private static <T extends Enum<T>> T getEnum(T[] values, String column, Cursor cursor) {
    	
    	int columnIndex = cursor.getColumnIndexOrThrow(column);
    	if ( cursor.isNull(columnIndex) ) {
    		return null;
    	}
    	
    	int ordinal = cursor.getInt(columnIndex);
    	return values[ordinal];
    	
    }
    
	private static int getInt(String column, Cursor c) {
		return c.getInt(c.getColumnIndexOrThrow(column));
	}	    

}
