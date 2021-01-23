@Override
public void teleopPeriodic() {
    // ...

    // Pathing Stuff
    long now = (System.currentTimeMillis() - start);

    int step = (int) Math.floor(now / clock); // index of path we're on or going through
    double substep = (now % clock) / clock; // % of the way through current path step

    if (path.size() <= step) {
        if (step == 0)
            path.add(new int[] { 0, 0 });
        else {
            // estimate position at time of step
            double leftDistance = ((encoder1.getDistance() - path.get(step - 1)[0]) * substep)
                    + path.get(step - 1)[0];
            double rightDistance = ((encoder2.getDistance() - path.get(step - 1)[1]) * substep)
                    + path.get(step - 1)[1];

            path.add(new int[] { (int) Math.floor(leftDistance), (int) Math.floor(rightDistance) });
        }
    }
}
