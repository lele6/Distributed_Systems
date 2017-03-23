package Project1Task2;

/**
 *
 * @author Le Le
 * @since September 23, 2016
 * This file is the Model part of the MVC. In this case, the business logic involves
 * recording the number of times each answer ("A", "B", "C", "D") has been submitted.
 */
public class ClassClickerModel {
    
    private int countA = 0; // number of times "A" has been submitted
    private int countB = 0; // number of times "B" has been submitted
    private int countC = 0; // number of times "C" has been submitted
    private int countD = 0; // number of times "D" has been submitted
    
    /**
     * This method is used to get the number of times "A" has been submitted
     * @return number of times "A" has been submitted
     */
    public int getCountA(){
        return countA;
    }
    
    /**
     * This method is used to get the number of times "B" has been submitted
     * @return number of times "B" has been submitted
     */
    public int getCountB(){
        return countB;
    }
    
    /**
     * This method is used to get the number of times "C" has been submitted
     * @return number of times "C" has been submitted
     */
    public int getCountC(){
        return countC;
    }
    
    /**
     * This method is used to get the number of times "D" has been submitted
     * @return number of times "D" has been submitted
     */
    public int getCountD(){
        return countD;
    }
    
    /**
     * This method is used to set the number of times "A" has been submitted
     * @param a 
     */
    public void setCountA(int a){
        countA = a;
    }
    
    /**
     * This method is used to set the number of times "B" has been submitted
     * @param b 
     */
    public void setCountB(int b){
        countB = b;
    }
    
    /**
     * This method is used to set the number of times "C" has been submitted
     * @param c
     */
    public void setCountC(int c){
        countC = c;
    }
    
    /**
     * This method is used to set the number of times "D" has been submitted
     * @param d
     */
    public void setCountD(int d){
        countD = d;
    }
    
    /**
     * This method is used to record the number of times each answer has been submitted
     * @param result 
     */
    public void recognizeAnswer(String result){
        if (result.equals("A"))
            countA++;
        else if(result.equals("B"))
            countB++;
        else if(result.equals("C"))
            countC++;
        else
            countD++;
    }
    
}
