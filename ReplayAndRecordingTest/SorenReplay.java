/*
// Autonomous
int clock;
long start;
ArrayList<int[]> path;
double correctionFactor;
*/

@Override
public void autonomousInit() {
    start = System.currentTimeMillis();
    clock = 500;
    correctionFactor = 0.25;
    path = new ArrayList<int[]>();

    Scanner sc;
    try {
        sc = new Scanner(new File("[FILEPATH]"));
    } catch (Exception e) {
        sc = new Scanner("0,0\n0,5\n10,20\n20,25\n30,30");
    }
    sc.useDelimiter(",");
    while (sc.hasNext()) {
        path.add(new int[] { sc.nextInt(), sc.nextInt() });
    }
    sc.close();
}

@Override
public void autonomousPeriodic() {
    long now = (System.currentTimeMillis() - start);

    int step = (int) Math.floor(now / clock); // index of path we're on or going through
    if (step < path.size() - 1) {
        int[] current = path.get(step);
        int[] next = path.get(step + 1);

        double substep = (now % clock) / clock; // % of the way through current path step

        double targetLeft = (next[0] - current[0]) * substep;
        int realLeft = encoder1.get() - current[0];
        if (realLeft == 0)
            realLeft = 1;
        double correctionLeft = targetLeft / realLeft;
        correctionLeft = ((correctionLeft - 1) * correctionFactor) + 1;

        double targetRight = (next[1] - current[1]) * substep;
        int realRight = encoder2.get() - current[1];
        if (realRight == 0)
            realRight = 1;
        double correctionRight = targetRight / realRight;
        correctionRight = ((correctionRight - 1) * correctionFactor) + 1;

        myDrive.tankDrive(correctionLeft, correctionRight);
    } else if (step < path.size()) {
        int[] target = path.get(step);

        double substep = (now % clock) / clock; // % of the way through current path step

        double targetLeft = target[0] * substep;
        int realLeft = encoder1.get();
        if (realLeft == 0)
            realLeft = 1;
        double correctionLeft = targetLeft / realLeft;
        correctionLeft = ((correctionLeft - 1) * correctionFactor) + 1;

        double targetRight = target[1] * substep;
        int realRight = encoder2.get();
        if (realRight == 0)
            realRight = 1;
        double correctionRight = targetRight / realRight;
        correctionRight = ((correctionRight - 1) * correctionFactor) + 1;

        myDrive.tankDrive(correctionLeft, correctionRight);
    } else {
        myDrive.tankDrive(0, 0);
    }
}
