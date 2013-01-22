
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

    private static int[] initialState;

    private static int[] indexHelper;

    static {
        updateGenomeInformation();
    }

    //State variables which should be unique between tests

    private int[] genome;
    private int[] genomeUsageStatistics;

    private int[] state;

    public CellularAutomataEngine() {
        genome = new int[genomeSize];
        genomeUsageStatistics = new int[genomeSize];
        System.arraycopy(initialState, 0, state, 0, initialState.length);
    }

    public CellularAutomataEngine(int[] genome) {
        if(genome.length != genomeSize) throw new RuntimeException("Genome length: "+genome.length+" Expected: "+genomeSize);
        this.genome = genome;
        genomeUsageStatistics = new int[genomeSize];
        System.arraycopy(initialState, 0, state, 0, initialState.length);
    }

    private static void updateGenomeInformation() {
        genomeSize = (int)Math.pow(nbStates, neighborhoodSize);

        indexHelper = new int[neighborhoodSize];
        for(int i = 0; i < neighborhoodSize;i++) indexHelper[i] = (int)Math.pow(nbStates, i);
    }

    public int getGenomeSize() {return genomeSize;}
    public static int[] getInitialState() {return initialState;}
    public static int getNbStates() {return nbStates;}
    public static int getNeighborhoodSize() {return neighborhoodSize;}
    public static boolean isWrapAround() {return wrapAround;}

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
        genome[index] = gene;
    }

    public int getGene(int[] neighborhoodConfiguration) {
        int index = getGeneIndex(neighborhoodConfiguration);
        return genome[index];
    }

    public int getGeneKeepStatistics(int[] neighborhoodConfiguration) {
        int index = getGeneIndex(neighborhoodConfiguration);
        genomeUsageStatistics[index]++;
        return genome[index];
    }

}
