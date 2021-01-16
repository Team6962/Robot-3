//this is a test
//
// go forward for 2 seconds
// stop for 2 seconds
// go backward for 2 seconds

if (now - start < 2000 {
      myDrive.tankDrive (0.65, 0.65)
}
else if (now - start < 4000 {
      myDrive.tankDrive (0,0)
}
else if (now - start < 6000 {
      myDrive.tankDrive (-0.65,-0.65)
}
else myDrive.tankDrive (0,0);
