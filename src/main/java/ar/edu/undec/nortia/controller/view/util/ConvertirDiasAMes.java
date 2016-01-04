/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.nortia.controller.view.util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Hugo
 */
public class ConvertirDiasAMes {
    
    public Integer convert(Date fecha, Integer dias){
        
        Integer meses = 0;
        int year = 2000;
        int numDays = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        switch (cal.get(Calendar.MONTH)){
            case 1: case 3: case 5:
            case 7: case 8: case 10:
            case 12:
                numDays = 31;
                break;
            case 4: case 6:
            case 9: case 11:
                numDays = 30;
                break;
            case 2:
                if (((year % 4 == 0) && 
                     !(year % 100 == 0))
                     || (year % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
            default:
                System.out.println("Invalid month.");
                break;
        }
        return meses;
     
    }
    
}
