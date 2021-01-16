long now = System.currentTimeMillis();
long time = (now - start) % 2000;
if (now - start > 7000) { // stop after ten seconds
    myDrive.tankDrive(0, 0);
} else if (time > 1750) { // otherwise do a lil dance that loops every 2 seconds
    myDrive.tankDrive(0.4, -0.4);
} else if (time > 1500) {
    myDrive.tankDrive(-0.4, 0.4);
} else if (time > 1250) {
    myDrive.tankDrive(0.4, -0.4);
} else if (time > 1000) {
    myDrive.tankDrive(-0.4, 0.4);
} else if (time > 750) {
    myDrive.tankDrive(0.4, 0.4);
} else if (time > 500) {
    myDrive.tankDrive(-0.4, -0.4);
} else if (time > 250) {
    myDrive.tankDrive(0.4, 0.4);
} else if (time > 0) {
    myDrive.tankDrive(-0.4, -0.4);
}
