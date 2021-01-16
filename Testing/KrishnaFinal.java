    if (now - start < 500){
      myDrive.tankDrive( 0.25, 0.25);
    }
    else if(now - start < 1000){
      myDrive.tankDrive(0.5,-0.5);
    }
    else if(now - start < 2000){
      myDrive.TankDrive(0.5,0.5);
    }
    else if(now - start < 2500){
      myDrive.TankDrive(0.65,0.65);
    }
    else if(now -start < 3500){
      myDrive.tankDrive(-0.65,0.65);
    }
    else if(now - start < 4000){
      myDrive.TankDrive(0.8,0.8);
    }
    else{
      myDrive.tankDrive(0, 0);
    }
