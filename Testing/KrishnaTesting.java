  public void autonomousPeriodic() {

    if (now - start < 500){
      myDrive.tankDrive( 0.25, 0.25);
    }
    else if(now - start < 1000){
      myDrive.tankDrive(0.5,0.5);
    }
    else if(now - start < 1500){
      myDrive.TankDrive(0.5,-0.5);
    }
    else if(now - start < 1600){
      myDrive.TankDrive(0.5,0.5);
    }
    else if(now -start < 1700){
      myDrive.tankDrive(-0.5,0.5);
    }
    else if(now - start < 1800){
      myDrive.TankDrive(0.5,0.5);
    }
    else{
      myDrive.tankDrive(0, 0);
    }
    
}
