  public void autonomousPeriodic() {

    if (now - start < 500){
      myDrive.tankDrive( 0.25, 0.25);
    }
    else if(now - start < 1000){
      myDrive.tankDrive(0.5,0.5);
    }
    else if(now - start < 1500){
      myDrive.TankDrive(0.5,-0.5);
      myDrive.TankDrive(0.25,0.25);
      }
    else if(now -s start < 2000){
      myDrive.tankDrive(-0.5,0.5);
      myDrive.tankDrive(0.25,0.25);
    }
    else{
      myDrive.tankDrive(0, 0);
    }
    
}
