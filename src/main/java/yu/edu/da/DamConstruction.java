package edu.yu.da;
import java.util.*;

/** Defines the API for specifying and solving the DamConstruction problem (see
 * the requirements document).
 *
 * Students MAY NOT change the public API of this class, nor may they add ANY
 * constructor.
 *
 * @author Avraham Leff
 */

public class DamConstruction {

    public class Permutations {
        public ArrayList<int[]> generatePermutations(int[] values) {
            ArrayList<int[]> result = new ArrayList<>();
            generatePermutationsHelper(values, 0, result);
            return result;
        }
        private void generatePermutationsHelper(int[] values, int index, ArrayList<int[]> result) {
            if (index == values.length) {
                result.add(Arrays.copyOf(values, values.length));
            } else {
                for (int i = index; i < values.length; i++) {
                    swap(values, index, i);
                    generatePermutationsHelper(values, index + 1, result);
                    swap(values, index, i);
                }
            }
        }
        private void swap(int[] values, int i, int j) {
            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }
    }
    private Map<Integer,Map<Integer,Integer>> optimalValues;
    private int[] y;
    int riverEnd;
    /** Constructor
     *
     * @param Y y-positions specifying dam locations, sorted by ascending
     * y-values.  Client maintains ownership of this parameter.  Y must contain
     * at least one element.
     * @param riverEnd the y-position of the river's end (a dam was previously
     * constructed both at this position and at position 0 and no evaluation will be
     * made of their construction cost): all values in Y are both greater than 0
     * and less than riverEnd.
     * @note students need not verify correctness of either parameter.  On the
     * other hand, for your own sake, I suggest that you add these (easy to do)
     * "sanity checks".
     */
    public DamConstruction(final int Y[], final int riverEnd) {
        for(int i=0;i<Y.length;i++){
            if(Y[i]<+0||Y[i]>=riverEnd){
                throw new IllegalArgumentException("the dam at index: "+i+" is out of range of the river");
            }
        }
        this.optimalValues=new HashMap<>();
        //this.matrix=new int[riverEnd+1][riverEnd+1];
        this.y=new int[Y.length+2];
        this.y[0]=0;
        this.y[Y.length+1]=riverEnd;
        for(int i=0;i<Y.length;i++){
            this.y[i+1]=Y[i];
        }
        this.riverEnd=riverEnd;
        // fill me in to taste
    } // constructor

    /** Solves the DamConstruction problem, returning the minimum possible cost
     * of evaluating the environmental impact of dam construction over all
     * possible construction sequences.
     *
     * @return the minimum possible evaluation cost.
     */
    public int solve() {
        for(int i=1;i<this.y.length;i++){
            int ind1=this.y[i-1];
            int ind2=this.y[i];
            if(optimalValues.get(ind1)==null){
                Map<Integer,Integer> m=new HashMap<>();
                m.put(ind2,0);
                optimalValues.put(ind1,m);
            }
            else{
                this.optimalValues.get(ind1).put(ind2,0);
            }
        }
        for(int i=2;i<y.length;i++){
            for(int j=0;j<y.length-i;j++){
                int leftDam=this.y[j];
                int rightDam=this.y[j+i];
                if(optimalValues.get(leftDam)==null){
                    Map<Integer,Integer> m=new HashMap<>();
                    m.put(rightDam,rightDam-leftDam+minOpt(leftDam,rightDam));
                    optimalValues.put(rightDam,m);
                }
                else{
                    this.optimalValues.get(leftDam).put(rightDam,rightDam-leftDam+minOpt(leftDam,rightDam));
                }
            }
        }
        return this.optimalValues.get(0).get(riverEnd);
    }

    private int minOpt(int leftDam,int rightDam){
        int min=Integer.MAX_VALUE;
        for(int d:this.y){
            if(d>leftDam&&d<rightDam){
                if(this.optimalValues.get(leftDam).get(d)+this.optimalValues.get(d).get(rightDam)<min){
                    min=this.optimalValues.get(leftDam).get(d)+this.optimalValues.get(d).get(rightDam);
                }
            }
            else if(d>=rightDam){break;}
        }
        return min;
    }
    /** Returns the cost of applying the dam evaluation decisions in the
     * specified order against the dam locations and river end state supplied to
     * the constructor.
     *
     * @param evaluationSequence elements of the Y parameter supplied in the
     * constructor, possibly rearranged such that the ith element represents the
     * y-position that is to be the ith dam evaluated for the WPA.  Thus: if Y =
     * {2, 4, 6}, damDecisions may be {4, 6, 2}: this method will return the cost
     * of evaluating the entire set of y-positions when dam evaluation is done
     * first for position "4", then for position "6", finally for position "2".
     * @return the cost of dam evaluation for the entire sequence of dam
     * positions when performed in the specified order.
     * @fixme This method is conceptually a static method because it doesn't
     * depend on the optimal solution produced by solve().  OTOH: the
     * implementation does require access to both the Y array and "river end"
     * information supplied to the constructor.
     * @note the implementation of this method is (almost certainly) not the
     * dynamic programming algorithm used in solve().  This method is part of the
     * API to stimulate your thinking as you work through this assignment and to
     * exercise your software engineering muscles.
     * @notetoself is this assignment too easy without an API for returning the
     * "optimal evaluation sequence"?
     */
    public int cost(final int[] evaluationSequence) {
        List<Integer> dams=new ArrayList<>();
        dams.add(0);
        dams.add(this.riverEnd);
        int cost=0;
        for(int i=0;i<evaluationSequence.length;i++){
            int dam=evaluationSequence[i];
            int insertionPoint=Math.abs(Collections.binarySearch(dams,dam)+1);
            cost+=dams.get(insertionPoint)-dams.get(insertionPoint-1);
            dams.add(insertionPoint,dam);
        }
        return cost;
    }
    public int solveUsingFactorial(){
        Permutations p=new Permutations();
        int[] myArr=new int[this.y.length-2];
        for(int i=1;i<this.y.length-1;i++){
            myArr[i-1]=this.y[i];
        }
        List<int[]>myArrays=p.generatePermutations(myArr);
        int minValue=Integer.MAX_VALUE;
        for(int[] arr:myArrays){
            int cost=cost(arr);
            if(cost<minValue){
                minValue=cost;
            }
        }
        return minValue;
    }
} // class
