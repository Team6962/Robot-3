long now = System.currentTimeMillis();
double speed = 0.65;
if (now - start > 7000) { // stop after seven seconds
    myDrive.tankDrive(0, 0);
} else if (now - start % 2000 > 1000) { // otherwise do a lil dance that loops every 2 seconds
    if (now - start % 500 > 250) {
        myDrive.tankDrive(speed, -speed);
    } else {
        myDrive.tankDrive(-speed, speed);
    }
} else {
    if (now - start % 500 > 250) {
        myDrive.tankDrive(-speed, -speed);
    } else {
        myDrive.tankDrive(speed, speed);
    }
}
