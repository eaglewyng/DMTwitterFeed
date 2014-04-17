package Feed;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class Stream {

	static int fileCount = 0;
	static int counter = 0;
	static FileWriter output;
	 /**
	  * 
	  * @param args Accepts the arguments specified in the argParser
	  * @throws TwitterException
	  * @throws IOException
	  */
	public static void main(String[] args) throws TwitterException, IOException
	{
		
        Stream stream = new Stream();
        stream.run(args);
        
          
	}
	
	private void run(String[] args){
		
		//parse the arguments and put them to use (ie store them in variables)
		ArgParser parser = new ArgParser(args);
		String outputFolderPath = parser.getoutputFolderPath();
		try{
			if(outputFolderPath == null){
				//assign the default
				outputFolderPath = "data";
			}
			output = new FileWriter(outputFolderPath + "/file-"+ fileCount++ + ".txt" );
			
		}
        catch(IOException e){
        	System.out.println("There was a problem accessing the folder " +
        			outputFolderPath + " for output data:\n"
        			+e.getMessage());
        	return;
        }
		
		String filterFilePath = parser.getFilterPath();
		if(filterFilePath == null){
			//assign the default
			filterFilePath = "RunInfo/filters.txt";
		}
		
		String authFilePath = parser.getAuthPath();
        if(authFilePath == null){
        	authFilePath = "RunInfo/auth.txt";
        }
        
        String[] authParams = getOAuthParameters(authFilePath);
        
        if(authParams.length != 4 || authParams == null){
        	System.out.println("There was an error while parsing your authentication detail file at "
        			+authFilePath + ":\nSyntax error in file or wrong number of oauth values");
        	return;
        }
		
        //start building the twitterstream
        ConfigurationBuilder cb = makeConfigurationBuilder(authParams[0], authParams[1], 
        		authParams[2], authParams[3]);
        

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        

        StatusListener listener = new StatusListener() {
            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void onDeletionNotice(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatus(Status status) {
            	try {
            		output.write(DataObjectFactory.getRawJSON(status) + "\n");
					counter++;
	            	if ( counter >= 5000){
	            		output.flush();
	            		output.close();
	            		output = new FileWriter("data/file-"+ fileCount++ + ".txt" );
	            		counter = 0;
	            	}
				} catch (IOException e) {	
					e.printStackTrace();
				}
            }

            @Override
            public void onTrackLimitationNotice(int arg0) {
                // TODO Auto-generated method stub

            }

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

        };
        FilterQuery fq = new FilterQuery();
        
        
        //process the file into an array
        String keywords[] = getFilters(filterFilePath);
        if(keywords == null){
        	System.out.println("There were no filters fount!");
        	return;
        }
        fq.track(keywords);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);
	}
	
	/**
	 * Gets the filters from the specified file and returns them in an array of strings
	 * @param filterFilePath The file path to the filter file
	 * @return An array of strings that are the keywords to filter from the stream
	 */
	private static String[] getFilters(String filterFilePath){
		File filterFile = new File(filterFilePath);
		Scanner filterScanner;
		try{
			filterScanner = new Scanner(filterFile);
		}
		catch(IOException e){
			return null;
		}
		List<String> filterList = new ArrayList<String>();
		
		while(filterScanner.hasNextLine()){
			String tempFiltString = filterScanner.nextLine();
			if(tempFiltString.length() == 0){
				continue;
			}
			filterList.add(tempFiltString);
		}
		
		filterScanner.close();
		
		String[] retArr = new String[filterList.size()];
		retArr = filterList.toArray(retArr);
		return retArr;
		
		
	}
	
	/**
	 * Creates the configuration builder
	 * @param consumerKey The consumer key for the app
	 * @param consumerSecret The consumer secret for the app
	 * @param accessToken The access token for the user
	 * @param accessTokenSecret The access token secret for the user
	 * @return A configured ConfigurationBuilder
	 */
	private static ConfigurationBuilder makeConfigurationBuilder(String consumerKey, String consumerSecret, 
			String accessToken, String accessTokenSecret){
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("consumerKey");
        cb.setOAuthConsumerSecret("consumerSecret");
        cb.setOAuthAccessToken("accessToken");
        cb.setOAuthAccessTokenSecret("accessTokenSecret");
        cb.setJSONStoreEnabled(true);
		return cb;
	}
	
	private static String[] getOAuthParameters(String oAuthPath){
		File oAuthFile = new File(oAuthPath);
		Scanner oAuthScanner;
		try{
			oAuthScanner = new Scanner(oAuthFile);
		}
		catch(IOException e){
			return null;
		}
		
		
		List<String> authList = new ArrayList<String>();
		while(oAuthScanner.hasNextLine()){
			String tempAuthString = oAuthScanner.nextLine();
			if(tempAuthString.length() == 0 || tempAuthString.startsWith("#")){
				continue;
			}
			authList.add(tempAuthString);
		}
		
		oAuthScanner.close();


		String[] retArr = new String[authList.size()];
		retArr = authList.toArray(retArr);
		return retArr;
	}
	
	/**
	 * A class for parsing the arguments
	 * @author pbridd
	 *
	 */
	private class ArgParser {
		
		String authPath;
		String filterPath;
		String outputFolderPath;
		

		//You can add more options for specific learning models if you wish
		public ArgParser(String[] argv) {
			authPath = null;
			filterPath = null;
			outputFolderPath = null;
			try{
	
			 	for (int i = 0; i < argv.length; i++) {

			 		if (argv[i].equals("-a")){
			 			authPath = argv[++i];
			 		}
			 		else if (argv[i].equals("-f")){
			 			filterPath = argv[++i];
			 		}
					else if (argv[i].equals("-o")){
						outputFolderPath = argv[++i];
					}
				
					else{
						System.out.println("Invalid parameter: " + argv[i]);
						System.exit(0);
					}
			  	}
		 
				}
				catch (Exception e) {
					System.out.println("Usage:");
					System.out.println("DMTwitterFeed -a [Path to oAuth information] -f [Path to filter information] -o [path to output folder]\n");
					System.exit(0);
				}
				
			}
	 
		//The getter methods
		public String getAuthPath(){ return authPath; }	
		public String getFilterPath(){ return filterPath; }	 
		public String getoutputFolderPath(){ return outputFolderPath; }	
		
	}

	
}


