package lld.machinecoding.bookmyshow;

/* ****** Copy this default code to your local code editor
and after completing solution, paste it back here for testing ******** */
import java.util.*;

public class MySolution {
    //private Helper10 helper;
    public MySolution(){}
    //public void init(Helper10 helper) {
     //   this.helper=helper;
    //}
    HashMap<Integer,List<Cinema>> cityToCinemas = new HashMap<>();

    HashMap<Integer,Cinema> cinemaDB = new HashMap<>();
    HashMap<Integer,Show> showDB = new HashMap<>();
    HashMap<Integer,Show> movieToShowDB = new HashMap<>();
    HashMap<Integer,Screen> showToScreenDB = new HashMap<>();
    HashMap<String,Ticket> ticketDB = new HashMap<>();
    HashMap<Integer,Set<Integer>> movieToCinemaDB = new HashMap<>();


    public class Cinema {
        public int cinemaId;
        public List<Screen> screen;
        public Cinema(){
            this.screen = new ArrayList<Screen>();
        }
        public Cinema(int cinemaId,List<Screen> screen){
            this.cinemaId=cinemaId;
            this.screen = screen;
        }
    }

    public class Screen {
        public int screenIndex;
        public Show show;
        public boolean[][] seets;
        public Screen(int screenIndex,int screenRow,int screenColumn){
            this.screenIndex = screenIndex;
            seets = new boolean[screenRow][screenColumn];
        }
        public Screen(int screenIndex, Show show){
            this.screenIndex = screenIndex;
            this.show = show;
        }

    }

    public class Show{
        public int showId;
        public int movieId;
        public long startTime;
        public long endTime;
        public Show(int showId,int movieId,long startTime,long endTime){
            this.showId=showId;
            this.movieId =movieId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public class Ticket{
        public String ticketId;
        public List<String> bookedSeets;
        public Screen screen;
        public Ticket(String ticketId ,List<String> bookedSeets,Screen screen){
            this.ticketId = ticketId;
            this.bookedSeets = bookedSeets;
            this.screen= screen;

        }
    }

    public void addCinema(int cinemaId, int cityId,
                          int screenCount, int screenRow, int screenColumn) {
        List<Screen> listOfScreen = new ArrayList<Screen>();

        for(int i=1;i<=screenCount;i++){
            Screen newScreen = new Screen(i,screenRow,screenColumn);
            listOfScreen.add(newScreen);
        }

        Cinema newCinema = new Cinema(cinemaId,listOfScreen);

        // add in DB
        cinemaDB.put(cinemaId,newCinema);
        // add to city
        cityToCinemas.computeIfAbsent(cityId,(k -> new ArrayList<>())).add(newCinema);

    }

    public void addShow(int showId, int movieId, int cinemaId,
                        int screenIndex, long startTime, long endTime) {
        Show newShow = new Show(showId,movieId,startTime,endTime);
        Cinema cinema = cinemaDB.get(cinemaId);
        Screen screen = cinema.screen.get(screenIndex);
        screen.show = newShow;

        //Add show in DB;
        showDB.put(showId,newShow);
        //Add movie to show
        movieToShowDB.put(movieId,newShow);
        //add show to screen
        showToScreenDB.put(showId,screen);

        movieToCinemaDB.computeIfAbsent(movieId,(k -> new HashSet<>())).add(cinemaId);


    }

    public List<String> bookTicket(String ticketId,
                                   int showId, int ticketsCount) {

        Screen screen = showToScreenDB.get(showId);
        List<String> result  = new ArrayList<>();

        if(screen!= null){
            int row=0;
            int col=0;

            int r = screen.seets.length;
            int c = screen.seets[0].length;
            for(int i=0;i<r;i++){
                for(int j=0;j<c;j++){
                    if(screen.seets[i][j]==false){
                        StringBuilder sb = new StringBuilder("");
                        sb.append(Integer.toString(i));
                        sb.append("-");
                        sb.append(Integer.toString(j));
                        String ans = sb.toString();
                        result.add(ans);
                        screen.seets[i][j] = true;
                        ticketsCount--;
                        if(ticketsCount ==0) {
                            Ticket t = new Ticket(ticketId,result,screen);
                            ticketDB.put(ticketId,t);
                            return result;
                        }

                    }

                }

            }

        }


        return result;
    }

    public boolean cancelTicket(String ticketId) {
        Ticket ticket = ticketDB.get(ticketId);
        if(ticket == null){
            return false;

        }
        for(String bookedSeet : ticket.bookedSeets){
            String[] rowCol = bookedSeet.split("-");
            int r = Integer.parseInt(rowCol[0]);
            int c = Integer.parseInt(rowCol[1]);
            ticket.screen.seets[r][c] = false;

        }

        return true;
    }

    public int getFreeSeatsCount(int showId) {
        int count =0;
        Screen screen = showToScreenDB.get(showId);
        if(screen == null){
            return 0;

        }
        for(int i=0;i<screen.seets.length;i++){
            for(int j=0;j<screen.seets[0].length;j++){
                if(screen.seets[i][j]== false){
                    count++;
                }
            }

        }

        return count;
    }

    public List<Integer> listCinemas(int movieId, int cityId) {

        Set<Integer> cinemaIds = movieToCinemaDB.get(movieId);
        List<Integer> result = new ArrayList<>();
        if(cinemaIds!= null){
            for(Cinema cinema : cityToCinemas.get(cityId)){
                if(cinemaIds.contains(cinema.cinemaId)){
                    result.add(cinema.cinemaId);
                }
            }
        }
        return result;
    }

    public List<Integer> listShows(int movieId, int cinemaId) {

        List<Integer> result = new ArrayList<>();
        Cinema cinema = cinemaDB.get(cinemaId);
        if(cinema != null){
            for(Screen sc : cinema.screen){
                if(sc.show.movieId == movieId){
                    result.add(sc.show.showId);
                }

            }

        }



        return result;
    }

}

// uncomment below code when you are using your local code editor and
// comment it back again back when you are pasting completed solution in the online CodeZym editor
// this will help avoid unwanted compilation errors and get method autocomplete in your local code editor.
/**
 interface Q10MovieBookingInterface{
 void init(Helper10 helper);
 void addCinema(int cinemaId, int cityId,
 int screenCount, int screenRow, int screenColumn);
 void addShow(int showId, int movieId, int cinemaId,
 int screenIndex, long startTime, long endTime);
 List<String> bookTicket(String ticketId,
 int showId, int ticketsCount);
 boolean cancelTicket(String ticketId);
 int getFreeSeatsCount(int showId);
 // returns cinemaId's of all cinemas which are running a show for given movie
 // cinemaId's are ordered in ascending order
 List<Integer> listCinemas(int movieId, int cityId);
 // returns all showId's of all shows displaying the movie in given cinema
 // showId's are ordered in ascending order
 List<Integer> listShows(int movieId, int cinemaId);

 }

 class Helper10{
 void print(String s){System.out.print(s);}
 void println(String s){print(s+"\n");}
 }
 */