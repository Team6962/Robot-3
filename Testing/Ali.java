  @Override
  public void autonomousPeriodic() {

    long now = System.currentTimeMillis();
    if(now - start < 2000){
      myDrive.tankDrive(-0.5, 0.5);
    } 
    else if(now - start < 3000){
      myDrive.tankDrive(-0.2,-0.4);
    }
    else if(now - start < 4000){
      myDrive.tankDrive(-0.4,-0.2);
    }
    else if(now - start < 5000){
      myDrive.tankDrive(-0.2,-0.4);
    }
    else if(now - start < 6000){
      myDrive.tankDrive(-0.4,-0.2);
    }
    else if(now - start < 7000){
      myDrive.tankDrive(-0.2,-0.4);
    }
    else if(now - start < 8000){
      myDrive.tankDrive(-0.2,-0.4);
    }
    else {
      myDrive.tankDrive(0, 0);
    }

  }
