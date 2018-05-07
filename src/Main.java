//Sam Carrillo
//2.11.18
//CSC 318 Lumber Company Simulation

/*
        This is a simulation of a lumber yard tree growth system. Input to the
    simulation consists of two types. Environmental effects describing droughts,
    beetles and forest wildfires. The second type represents policy variables
    for spraying the trees with insecticide and seeding the clouds. In this case the
    variables include the percentage of 3 year old trees sprayed, the percentage of 4 year old
    trees sprayed, and if we seed the clouds to change rain distribution.
    Output from the program will consist of the mean and variance for all
    acres of trees in equivalent age categories 1, 2, 3 and 4.

 */
public class Main {

    public static void main(String[] args) {
        int year, fire, rainfall;

        //***** Variables *****
        double maturedOneTrees, maturedTwoTrees, maturedThreeTrees, maturedFourTrees;
        double immatureOneTrees, immatureTwoTrees, immatureThreeTrees, immatureFourTrees;
        double targetTrees, totaltrees = 0, totaltrees2 = 0, onetotal = 0, onetotal2 = 0, twototal = 0, twototal2 = 0, threetotal = 0, threetotal2 = 0, fourtotal = 0;
        double totalaverage, totalstddev, totalvariance, oneaverage, twoaverage, threeaverage, fouraverage, fourtotal2 = 0;
        double onestddev, twostddev, threestddev, fourstddev, onevariance, twovariance, threevariance, fourvariance;

        //percentage of trees that survive drought
        double drought1[] = {.90, .95, .98};
        double drought2[] = {.90, .95, .97};
        double drought3[] = {.70, .95, .97};
        double drought4[] = {.65, .95, .96};

        //percentage of trees that die by beetles
        double beetles1[] = {.10, .05, 0};
        double beetles2[] = {.15, .05, 0};
        double beetles3[] = {.30, .10, .02};
        double beetles4[] = {.30, .10, .02};

        //percentage of trees that survive fires
        double fire1[] = {.85, .90, .95};
        double fire2[] = {.82, .88, .93};
        double fire3[] = {.78, .85, .90};
        double fire4[] = {.70, .80, .85};

        //Fraction of trees that grow to maturity
        double grow1[] = {.20, .94, .96};
        double grow2[] = {.15, .93, .94};
        double grow3[] = {.1, .93, .94};
        double grow4[] = {0, .94, .92};

        //create the tree actors
        Tree oneTree = new Tree(400000, 0, drought1, beetles1, grow1, fire1);//invuln trees figured out elsewhere
        Tree twoTree = new Tree(300000, 0, drought2, beetles2, grow2, fire2);//^
        Tree threeTree = new Tree(200000, 0, drought3, beetles3, grow3, fire3);//^
        Tree fourTree = new Tree(100000, 0, drought4, beetles4, grow4, fire4);//^

        //run the time step loop for 100 years
        for (year = 1; year < 100; year++) {
            //determine the rainfall & fire for the year
            rainfall = regularRainfall();
            fire = wildFire();
            //Policy A: Seeded rainfall
            //rainfall = seededRainfall();

            //calculate trees surviving the weather
            oneTree.survDrought(rainfall);
            twoTree.survDrought(rainfall);
            threeTree.survDrought(rainfall);
            fourTree.survDrought(rainfall);

            //calculate trees killed/affected by beetles
            oneTree.survBeetles(rainfall);
            twoTree.survBeetles(rainfall);
            threeTree.survBeetles(rainfall);
            fourTree.survBeetles(rainfall);

            //calculate trees surviving wildfires
            oneTree.survFire(fire);
            twoTree.survFire(fire);
            threeTree.survFire(fire);
            fourTree.survFire(fire);

            //calculate the trees that mature
            oneTree.mature(rainfall);
            twoTree.mature(rainfall);
            threeTree.mature(rainfall);
            fourTree.mature(rainfall);

            //calc tree population statistics
            oneTree.calcStats();
            twoTree.calcStats();
            threeTree.calcStats();
            fourTree.calcStats();

            //set variables for the aging process & statistics, mature and immature of each actor group
            maturedOneTrees = oneTree.getRegTrees() + oneTree.getInvulnTrees();
            maturedTwoTrees = twoTree.getRegTrees() + twoTree.getInvulnTrees();
            maturedThreeTrees = threeTree.getRegTrees() + threeTree.getInvulnTrees();
            maturedFourTrees = fourTree.getRegTrees() + fourTree.getInvulnTrees();

            immatureOneTrees = oneTree.getNewRegTrees() + oneTree.getNewInvulnTrees();
            immatureTwoTrees = twoTree.getNewRegTrees() + twoTree.getNewInvulnTrees();
            immatureThreeTrees = threeTree.getNewRegTrees() + threeTree.getNewInvulnTrees();
            immatureFourTrees = fourTree.getNewRegTrees() + fourTree.getNewInvulnTrees();

            if ((year >= 15) && (year <= 20)) {
                //print the population for these years' trees
                System.out.println("For year: " + year + ", Mature One year old: " + maturedOneTrees + ", Immature: " + immatureOneTrees);
                System.out.println("For year: " + year + ", Mature Two year old: " + maturedTwoTrees + ", Immature: " + immatureTwoTrees);
                System.out.println("For year: " + year + ", Mature Three year old: " + maturedThreeTrees + ", Immature: " + immatureThreeTrees);
                System.out.println("For year: " + year + ", Mature Four year old: " + maturedFourTrees + ", Immature: " + immatureFourTrees);
                System.out.println("---------------------------------------------------------------");
            }

            //find the total trees available for harvest this year (this is used in calculations to be thought of later)
            totaltrees += maturedOneTrees + immatureOneTrees + maturedTwoTrees + immatureTwoTrees + maturedThreeTrees + immatureThreeTrees + maturedFourTrees + immatureFourTrees;
            totaltrees2 += totaltrees * totaltrees;
            //the totals and their squares
            onetotal += maturedOneTrees + immatureOneTrees;
            onetotal2 += onetotal * onetotal;
            twototal += maturedTwoTrees + immatureTwoTrees;
            twototal2 += twototal * twototal;
            threetotal += maturedThreeTrees + immatureThreeTrees;
            threetotal2 += threetotal * threetotal;
            fourtotal += maturedFourTrees + immatureFourTrees;
            fourtotal2 += fourtotal * fourtotal;

            //plant the one year old trees to replace the harvest trees
            oneTree.addTrees(fourTree.getRegTrees(), fourTree.getInvulnTrees());

            //now kill the harvest trees
            fourTree.killTrees(fourTree.getRegTrees(), fourTree.getInvulnTrees());

            //Policy B: spray the three year olds to make them invulnerable to the beetle
            //threeTree.setInvulnTrees(threeTree.getRegTrees());
            //Policy C: Spray 50% of the 3's and 50% of the 4's
            threeTree.setInvulnTrees((threeTree.getRegTrees())/2);
            fourTree.setInvulnTrees((fourTree.getRegTrees())/2);

            //move back through the trees to age them
            oneTree.setTrees(immatureOneTrees + maturedFourTrees, 0);
            twoTree.setTrees(immatureTwoTrees + maturedOneTrees, 0);
            threeTree.setTrees(immatureThreeTrees + maturedTwoTrees, 0);
            fourTree.setTrees(immatureFourTrees + maturedThreeTrees, 0);

        }
        //calculate the statistics
        totalaverage = totaltrees / 100;
        totalvariance = (totaltrees2 / 100) - (totalaverage * totalaverage / 100);
        totalstddev = Math.sqrt(totalvariance);
        //ones
        oneaverage = onetotal / 100;
        onevariance = (onetotal2 / 100) - (oneaverage * oneaverage / 100);
        onestddev = Math.sqrt(onevariance);
        //twos
        twoaverage = twototal / 100;
        twovariance = (twototal2 / 100) - (twoaverage * twoaverage / 100);
        twostddev = Math.sqrt(twovariance);
        //threes
        threeaverage = threetotal / 100;
        threevariance = (threetotal2 / 100) - (threeaverage * threeaverage / 100);
        threestddev = Math.sqrt(threevariance);
        //fours
        fouraverage = fourtotal / 100;
        fourvariance = (fourtotal2 / 100) - (fouraverage * fouraverage / 100);
        fourstddev = Math.sqrt(fourvariance);
        //print out statistics
        System.out.println("Average Total Trees: " + totalaverage);
        System.out.println("Variance Total Trees: " + totalvariance);
        System.out.println("Total Tree Standard Deviation: " + totalstddev);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Average One Year old Trees: " + oneaverage);
        System.out.println("Variance One Year old Trees: " + onevariance);
        System.out.println("One Year old Tree Standard Deviation: " + onestddev);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Average Two Year old Trees: " + twoaverage);
        System.out.println("Variance Two Year old Trees: " + twovariance);
        System.out.println("Two Year old Tree Standard Deviation: " + twostddev);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Average Three Year old Trees: " + threeaverage);
        System.out.println("Variance Three Year old Trees: " + threevariance);
        System.out.println("Two Year old Tree Standard Deviation: " + threestddev);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Average Four Year old Trees: " + fouraverage);
        System.out.println("Variance Four Year old Trees: " + fourvariance);
        System.out.println("Four Year old Tree Standard Deviation: " + fourstddev);
    }

    //***** Process Generators *****
    public static int regularRainfall() {
        //this function is a process generator for rainfall
        //it returns 0 for drought, 1 for moderate, and 2 for heavy
        int x, inches, rainfall;
        x = (int) (Math.random() * 100);

        if (x == 1) {
            inches = 1;
        } else if (x < 6) {
            inches = 2;
        } else if (x < 11) {
            inches = 3;
        } else if (x < 14) {
            inches = 4;
        } else if (x < 24) {
            inches = 5;
        } else if (x < 39) {
            inches = 6;
        } else if (x < 59) {
            inches = 7;
        } else if (x < 73) {
            inches = 8;
        } else if (x < 83) {
            inches = 9;
        } else if (x < 93) {
            inches = 10;
        } else if (x < 98) {
            inches = 11;
        } else {
            inches = 12;
        }

        if (inches <= 3) {
            rainfall = 0;//drought
        } else if (inches <= 10) {
            rainfall = 1;//moderate
        } else {
            rainfall = 2;//heavy
        }
        return rainfall;
    }

    public static int seededRainfall() {
        //this function is a process generator for seeded rainfall
        //it returns 0 for drought, 1 for moderate, and 2 for heavy
        int x, inches, rainfall;
        x = (int) (Math.random() * 100);

        if (x == 1) {
            inches = 1;
        } else if (x < 2) {
            inches = 2;
        } else if (x < 3) {
            inches = 3;
        } else if (x < 5) {
            inches = 4;
        } else if (x < 15) {
            inches = 5;
        } else if (x < 25) {
            inches = 6;
        } else if (x < 50) {
            inches = 7;
        } else if (x < 65) {
            inches = 8;
        } else if (x < 75) {
            inches = 9;
        } else if (x < 85) {
            inches = 10;
        } else if (x < 95) {
            inches = 11;
        } else {
            inches = 12;
        }

        if (inches <= 3) {
            rainfall = 0;//drought
        } else if (inches <= 10) {
            rainfall = 1;//moderate
        } else {
            rainfall = 2;//heavy
        }
        return rainfall;
    }

    public static int wildFire() {
        //process generator for the fire
        //drought(1"-3"):35
        //moderate(4"-10"):97
        //heavy(11"+):97+
        int x, fire;
        x = (int) (Math.random() * 100);

        if (x <= 35) {
            fire = 0;
        } else if (x < 97) {
            fire = 1;
        } else {
            fire = 2;
        }

        return fire;
    }

}

class Tree {
    /*
        This class represents trees for one age group. The simulation will use
        4 objects of this class representing the trees of ages 1, 2, 3 and 4.
        The class goes through all of the functions of tree each year growth in drought,
        surviving beatles, and surviving fires.
    */

    private double regTrees;//the current number of regular trees
    private double invulnTrees;//the current number of beetle invulnerable trees
    private double newRegTrees;//the current number of new trees
    private double newInvulnTrees;//the current number of new beetle invulnerable trees
    private double sumNewTrees;//the sum of new trees
    private double sumNewTrees2;//the sum of new trees squared
    private double sumTrees; //the sum of the trees for this group
    private double sumTrees2;//the square of the sum of trees
    private double drought[] = {0, 0, 0};//the percent of trees affected by drought
    private double beetles[] = {0, 0, 0};//the percent of trees affected by beetles
    private double fire[] = {0, 0, 0};//the percent of trees destroyed by fire
    private double grow[] = {0, 0, 0};//the percentage of trees that grow to maturity

    //***** Functions for Trees *****
    public Tree(double _newTrees, double _newInvulnTrees, double[] _drought, double[] _beetles, double[] _grow, double[] _fire) {
        //Constructor for trees, this constructor initializes the original number of trees
        //and then the number that will die from various causes.
        int i;

        //set the number of trees originally in this group
        newRegTrees = _newTrees;
        newInvulnTrees = _newInvulnTrees;

        //this picks the percentage of survivors from the double arrays in the main, based on the input.
        for (i = 0; i <= 2; i++) {
            drought[i] = _drought[i];
            beetles[i] = _beetles[i];
            grow[i] = _grow[i];
            fire[i] = _fire[i];
        }

        //now set the statistics for the year for this group
        sumTrees = sumTrees2 = 0;
    }//end Tree constructor

    public void survDrought(int rainfallvalue) {
        //this function determines the number of trees growing considering drought percentage
        newRegTrees *= drought[rainfallvalue];
        newInvulnTrees *= drought[rainfallvalue];
        regTrees *= drought[rainfallvalue];
        invulnTrees *= drought[rainfallvalue];
        return;
    }

    public void survFire(int firevalue) {
        //this function calculates the number of trees surviving the fire
        newRegTrees *= fire[firevalue];
        newInvulnTrees *= fire[firevalue];
        regTrees *= fire[firevalue];
        invulnTrees *= fire[firevalue];
        return;
    }

    public void survBeetles(int rainfallvalue) {
        //this function determines the number of trees surviving beetles
        newRegTrees *= 1.0 - beetles[rainfallvalue];
        regTrees *= 1.0 - beetles[rainfallvalue];
        return;
    }

    public void mature(int mature) {
        //this function determines % of trees that mature, updates the new trees
        //and introduces them to the regTree population
        regTrees = newRegTrees * grow[mature];
        newRegTrees -= regTrees;
        invulnTrees = newInvulnTrees * grow[mature];
        newInvulnTrees -= invulnTrees;
    }


    public void killTrees(double _regTrees, double _invulnTrees) {
        //this function kills a number of both regular and
        //beetle invulnerable trees in a defined group
        regTrees -= _regTrees;
        invulnTrees -= _invulnTrees;
        return;
    }

    public void addTrees(double regular, double invulnerable) {
        //this function adds trees to a defined group
        regTrees += regular;
        invulnTrees += invulnerable;
        return;
    }

    public void setTrees(double regular, double invulnerable) {
        //this function sets the number of trees in a defined group
        regTrees = regular;
        invulnTrees = invulnerable;
        return;
    }

    public void calcStats() {
        //calculates the sum of the trees
        sumTrees += regTrees + invulnTrees;
        sumTrees2 += sumTrees * sumTrees;
    }

    //***** Getters and Setters for Trees *****
    public double getRegTrees() {
        return regTrees;
    }

    public void setRegTrees(double regTrees) {
        this.regTrees = regTrees;
    }

    public double getInvulnTrees() {
        return invulnTrees;
    }

    public void setInvulnTrees(double invulnTrees) {
        this.invulnTrees = invulnTrees;
    }

    public double getNewRegTrees() {
        return newRegTrees;
    }

    public void setNewRegTrees(double newRegTrees) {
        this.newRegTrees = newRegTrees;
    }

    public double getNewInvulnTrees() {
        return newInvulnTrees;
    }

}
