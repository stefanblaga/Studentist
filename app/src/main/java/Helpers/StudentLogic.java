package Helpers;

/**
 * Created by mario on 02/07/2017.
 */

public class StudentLogic {

    private  static StudentLogic instance  = null;

    public static StudentLogic getInstance()
    {
        if(instance == null)
            instance = new StudentLogic();

        return  instance;
    }



}
