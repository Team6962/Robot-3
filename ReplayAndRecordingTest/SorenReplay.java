/*
// Autonomous
int clock;
long start;
int[][] path;
double correctionFactor;
*/

@Override
public void autonomousInit() {
    start = System.currentTimeMillis();
    clock = 500;
    correctionFactor = 0.25;
    path = new int[][] { { 0, 0 }, { 0, 5 }, { 10, 20 }, { 20, 20 } };
}

@Override
public void autonomousPeriodic() {
    long now = (System.currentTimeMillis() - start);

    int step = (int) Math.floor(now / clock); // index of path we're on or going through
    int[] current = path[step];
    int[] next = path[step + 1];

    double substep = (now % clock) / clock; // % of the way through current path step

    double targetLeft = (next[0] - current[0]) * substep;
    int realLeft = encoder1.get() - current[0];
    double correctionLeft = targetLeft / realLeft;
    correctionLeft = ((correctionLeft - 1) * correctionFactor) + 1;

    double targetRight = (next[0] - current[0]) * substep;
    int realRight = encoder2.get() - current[0];
    double correctionRight = targetRight / realRight;
    correctionRight = ((correctionRight - 1) * correctionFactor) + 1;

    myDrive.tankDrive(correctionLeft, correctionRight);
}
