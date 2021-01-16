if (now - start < 1500) {
  myDrive.tankDrive(0.8, 0.8); // Move forward for 1.5 sec
} else if (now - start < 2000) {
  myDrive.tankDrive(0, 0);     // Pause for 0.5 sec
} else if (now - start < 3000) {
  myDrive.tankDrive(-0.8, 0.8);    // Turn left for 1 sec
} else if (now - start < 3500) {
  myDrive.tankDrive(0, 0);     // Pause for 0.5 sec
} else if (now - start < 5500) {
  myDrive.tankDrive(0.8, -0.8);    // Turn right for 2 sec
} else if (now - start < 6000) {
  myDrive.tankDrive(0, 0);     // Pause for 0.5 sec
} else if (now - start < 7000) {
  myDrive.tankDrive(-0.8, 0.8);    // Turn left for 1 sec
} else if (now - start < 7500) {
  myDrive.tankDrive(0, 0);     // Pause for 0.5 sec
} else if (now - start < 9000) {
  myDrive.tankDrive(-0.8, -0.8);   // Move Backwards for 1.5 sec
} else {
  myDrive.tankDrive(0, 0);
}
