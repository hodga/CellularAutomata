
package engine;

/**
 *
 * @author Hodga
 */
public class CellularAutomataEngine {
    
    //Structurally defining variables of the CA. these should be defined constant for each test
    
    /*
     * The number of states can be any positive integer number. 
     * However be mindful that a high number of states leads to a exponentially 
     * higher genome size.
     */
    private static int nbStates = 3;

    /*
     * NEIGHBORHOOD_SIZE can be:
     * 3 for 1-dimensional
     * 5 or 9 for 2-dimensional
     * 7 or 27 for 3-dimensional
    */
    private static int neighborhoodSize = 5;

    /*
     * WRAP_AROUND maps the edges of the grid to the edges on the other side
     * EDGE_STATE gives the cells outside the edge of the grid the same state
    */
    private static boolean wrapAround = true;
    private static byte edgeState = 0;

    private static int genomeSize;

    private static int[] dim;
    private static int[] initialState;

    private static int[] indexHelper;

    //State variables which should be unique between tests

    private int[] genome;
    private int[] genomeUsageStatistics;

    private int[] state;

    public CellularAutomataEngine() {
        genome = new int[genomeSize];
        genomeUsageStatistics = new int[genomeSize];
        state = new int[initialState.length];
        System.arraycopy(initialState, 0, state, 0, initialState.length);
    }

    public CellularAutomataEngine(int[] genome) {
        if(genome.length != genomeSize) throw new RuntimeException("Genome length: "+genome.length+" Expected: "+genomeSize);
        this.genome = genome;
        genome[0] = 0; //the all 0 neighborhood should always result in 0
        
        genomeUsageStatistics = new int[genomeSize];
        state = new int[initialState.length];
        System.arraycopy(initialState, 0, state, 0, initialState.length);
    }

    private static void updateGenomeInformation() {
        genomeSize = (int)Math.pow(nbStates, neighborhoodSize);
        indexHelper = new int[neighborhoodSize];
        for(int i = 0; i < neighborhoodSize;i++) indexHelper[i] = (int)Math.pow(nbStates, i);
    }

    public static int getGenomeSize() {return genomeSize;}
    public static int[] getInitialState() {return initialState;}
    public static int getNbStates() {return nbStates;}
    public static int getNeighborhoodSize() {return neighborhoodSize;}
    public static boolean isWrapAround() {return wrapAround;}
    public static int[] getDim() {return dim;}
    
    public static void setDim(int[] dim) {
        CellularAutomataEngine.dim = dim;
    }
    public static void setInitialState(int[] intialState) {
        CellularAutomataEngine.initialState = intialState;
    }
    public static void setNbStates(int nbStates) {
        CellularAutomataEngine.nbStates = nbStates;
        updateGenomeInformation();
    }
    public static void setNeighborhoodSize(int neighborhoodSize) {
        CellularAutomataEngine.neighborhoodSize = neighborhoodSize;
        updateGenomeInformation();
    }
    public static void setWrapAround(boolean wrapAround) {
        CellularAutomataEngine.wrapAround = wrapAround;
    }
    public static boolean verifyDim() {
        if(dim.length == 1 && neighborhoodSize != 3) {
            System.out.println("expecting neighborhood size 3 for 1 dimentional CA");
            return false;
        }
        if(dim.length == 2 && (neighborhoodSize != 5 || neighborhoodSize != 9)) {
            System.out.println("expecting neighborhood size 5 or 9 for 2 dimentional CA");
            return false;
        }
        if(dim.length == 3 && (neighborhoodSize != 7 || neighborhoodSize != 27)) {
            System.out.println("expecting neighborhood size 7 or 27 for 3 dimentional CA");
            return false;
        }
        
        int size = 1;
        for(int i = 0; i < dim.length;i++) size *= dim[i];
        if(size != initialState.length) {
            System.out.println("the length of the intialState array and the dimentions do not match");
            return false;
        }
        return true;
    }
    
    public int[] getState() {
        return state;
    }

    public int[] getGenomeUsageStatistics() {
        return genomeUsageStatistics;
    }
    
    public int[] getGenome() {
        return genome;
    }

    private int getGeneIndex(int[] neighborhoodConfiguration) {
        int index = 0;
        for(int i = 0; i < neighborhoodSize;i++)
        {
            if(neighborhoodConfiguration[i] != 0)index += neighborhoodConfiguration[i] * indexHelper[i];
        }
        return index;
    }

    public void putGene(int[] neighborhoodConfiguration, int gene) {
        int index = getGeneIndex(neighborhoodConfiguration);
        if(index != 0) genome[index] = gene;
    }

    public int getGene(int[] neighborhoodConfiguration) {
        int index = getGeneIndex(neighborhoodConfiguration);
        return genome[index];
    }

    private int getGeneKeepStatistics(int[] neighborhoodConfiguration) {
        int index = getGeneIndex(neighborhoodConfiguration);
        genomeUsageStatistics[index]++;
        return genome[index];
    }
    
    public void step() {
        int[] nextState = new int[state.length];
        int[] neighborhood = new int[neighborhoodSize];
        int over = 0, under = 0, below = 0, above = 0;
        
        for(int i = 0; i < state.length;i++) {
            
            int left = -1;
            int right = 1;
            
            if(wrapAround) {
                int x = i % dim[0];           
                if(x == 0) left = (dim[0] - 1);
                else if(x == dim[0]-1) right = -(dim[0] - 1);
            } 
            else {
                int x = i % dim[0];           
                if(x == 0) left = 0;
                else if(x == dim[0]-1) right = 0;
            }
            
            if(neighborhoodSize != 3) {
                over = -dim[0];
                under = dim[0];
                
                if(wrapAround) {
                    int y = (i/dim[0]) % dim[1];
                    if(y == 0) over = (dim[0] * (dim[1]-1));
                    else if(y == (dim[1]-1)) under = -(dim[0] * (dim[1]-1));
                } 
                else {
                    int y = (i/dim[0]) % dim[1];
                    if(y == 0) over = 0;
                    else if(y == (dim[1]-1)) under = 0;
                }
                
                if(neighborhoodSize == 27 || neighborhoodSize == 7) {
                    below = (dim[0]*dim[1]);
                    above = -below;
                    
                    if(wrapAround) {
                        int z = i /(dim[0]*dim[1]);
                        if(z == 0) above = ( dim[0]*dim[1] * (dim[2]-1));
                        else if(z == (dim[2]-1)) below = -(dim[0]*dim[1] * (dim[2]-1));
                    } 
                    else {
                        int z = i /(dim[0]*dim[1]);
                        if(z == 0) above = 0;
                        else if(z == (dim[2]-1)) below = 0;
                    }
                }
            }
            
            switch(neighborhoodSize) {
                case 3:
                    neighborhood[0] = (left == 0) ? edgeState : state[i+left];
                    neighborhood[1] = state[i];
                    neighborhood[2] = (right == 0) ? edgeState : state[i+right];
                    break;
                case 5:
                    neighborhood[0] = (over == 0) ? edgeState : state[i+over];
                    neighborhood[1] = (left == 0) ? edgeState : state[i+left];
                    neighborhood[2] = state[i];
                    neighborhood[3] = (right == 0) ? edgeState : state[i+right];
                    neighborhood[4] = (under == 0) ? edgeState : state[i+under];
                    break;
                case 9:
                    if(over == 0) neighborhood[0] = neighborhood[1] = neighborhood[2] = edgeState;
                    else {
                        neighborhood[0] = (left == 0) ? edgeState : state[i+over+left];
                        neighborhood[1] =  state[i+over];
                        neighborhood[2] = (right == 0) ? edgeState : state[i+over+right];
                    }
                    
                    neighborhood[3] = (left == 0) ? edgeState : state[i+left];
                    neighborhood[4] = state[i];
                    neighborhood[5] = (right == 0) ? edgeState : state[i+right];
                    
                    if(under == 0) neighborhood[6] = neighborhood[7] = neighborhood[8] = edgeState;
                    else {
                        neighborhood[6] = (left == 0) ? edgeState : state[i+under+left];
                        neighborhood[7] =  state[i+under];
                        neighborhood[8] = (right == 0) ? edgeState : state[i+under+right];
                    }
                    break;
                case 7:
                    neighborhood[0] = (above == 0) ? edgeState : state[i+above];
                    
                    neighborhood[1] = (over == 0) ? edgeState : state[i+over];
                    
                    neighborhood[2] = (left == 0) ? edgeState : state[i+left];
                    neighborhood[3] = state[i];
                    neighborhood[4] = (right == 0) ? edgeState : state[i+right];
                    
                    neighborhood[5] = (under == 0) ? edgeState : state[i+under];
                    
                    neighborhood[6] = (below == 0) ? edgeState : state[i+below];
                    break;
                case 27:
                    int ctr = 0;
                    int[][] hA = {{above, 0, below}, {over, 0, under}, {left, 0, right}};
                    boolean aD, bD;
                    for(int z = 0; z < 3;z++) {
                        if(z != 1 && hA[0][z] == 0) aD = false;
                        else aD = true;
                        for(int y = 0; y < 3; y++) {
                            if(y != 1 && hA[1][y] == 0) bD = false;
                            else bD = true;
                            for(int x = 0; x < 3; x++) {
                                if((x == 1 || hA[2][x] != 0) && aD && bD) neighborhood[ctr++] = edgeState;
                                else {
                                    neighborhood[ctr++] = state[i+hA[0][z]+hA[0][z]+hA[0][z]];
                                }
                            }
                        }
                    }
                    break;
            }
            
            nextState[i] = getGeneKeepStatistics(neighborhood);
        }
        state = nextState;
    }

}
