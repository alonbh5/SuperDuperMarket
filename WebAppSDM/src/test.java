import com.google.gson.Gson;
import course.java.sdm.engine.SuperDuperMarketSystem;

public class test {

    public static void foo () {
        SuperDuperMarketSystem s = new SuperDuperMarketSystem();
        Gson das = new Gson();
        System.out.println(s.toString()+das.toString());
    }

    public static void main(String[] args) {
        foo();
    }
}
