/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package examples;

import java.sql.SQLException;
import muvis.database.MusicLibraryDatabaseManager;

/**
 * Class with a simple testing for the MusicLibraryDatabaseManager
 * @author Ricardo
 */
public class TestMusicDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			MusicLibraryDatabaseManager dbManager = new MusicLibraryDatabaseManager();
			dbManager.initDatabase();

			//new track to add
			String [] filenames = new String[]{
					"C:\\album1\\track1-alicia.mp3",
					"C:\\album1\\track2-alicia.mp3",
					"C:\\album1\\track3-alicia.mp3",
					"C:\\album2\\track1-alicia.mp3"
			};
			String [] artistNames = new String[]{
					"Alicia Keys",
					"Alicia Keys",
					"Alicia Keys",
					"Alicia Keys"
					};
			String [] albumNames = new String[]{
					"As I Am",
					"As I Am",
					"As I Am",
					"Songs in A minor"
					};
			double[] trackDescriptor = new double[80];
			for(int i = 0; i< 80; i++)
				trackDescriptor[i] = i;

			for(int j = 0; j < filenames.length; j++){
				/*dbManager.addNewSong(filenames[j], artistNames[j],
						albumNames[j]);*/
			}

			dbManager.shutdown();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
