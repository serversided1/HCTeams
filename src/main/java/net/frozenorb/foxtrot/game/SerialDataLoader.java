package net.frozenorb.foxtrot.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.frozenorb.foxtrot.serialization.ReflectionSerializer;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

/**
 * Extensible, abstract class used to quickly manage JSON objects and load or
 * save it to files.
 * 
 * @author Kerem Celik
 * 
 */
public abstract class SerialDataLoader extends ReflectionSerializer {

	/**
	 * Base directory for server-related files and folders.
	 */
	public static transient File DATA_FOLDER = new File("data");

	protected transient BasicDBObject data = new BasicDBObject();
	private transient File file;

	/**
	 * Creates a new instance of the manager, and registers the file for use
	 * with it
	 * 
	 * @param f
	 *            the file that contains the data, and that will be saved to
	 */
	public SerialDataLoader(File f) {
		this.file = f;
		f.getParentFile().mkdir();

		if (!f.exists()) {
			try {
				f.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		loadData();
		saveData();

	}

	/**
	 * Gets the current data of the manager
	 * 
	 * @return the data
	 */
	public final BasicDBObject getData() {
		return data;
	}

	/**
	 * Sets the current data of the data manager
	 * 
	 * @param data
	 *            the data to set to
	 */
	public void setData(BasicDBObject data) {
		this.data = data;
	}

	/**
	 * Loads the data from a file
	 * <p>
	 * The method is final because you should never use anything but JSON. <br>
	 * Loads a new {@link BasicDBObject} if the file's contents are not
	 * JSON-parsable
	 */
	public final void loadData() {

		try {

			String fileData = FileUtils.readFileToString(file);

			data = (BasicDBObject) JSON.parse(fileData);
			if (data == null) {
				data = new BasicDBObject();
			}

			onLoad();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the current data to a file
	 * <p>
	 * Left as overridable so you can do your custom loading
	 */
	public void saveData() {
		if (data == null)
			return;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(data.toString());

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Abstract method that is called when the data is either loaded or
	 * reloaded, use to handle updates.
	 * <p>
	 * Normally called whenever {@link SerialDataLoader#loadData()} is called or
	 * is loaded from somewhere else
	 */
	protected abstract void onLoad();
}
