long now = System.currentTimeMillis();
    if((encoder1.getDistance() >= -200 || encoder1.getDistance() >= -200)&&now-start <8000){
      myDrive.tankDrive(-0.7, -0.7);
    }else if(now-start <8000) {
      outtake.set(0.65);
      transfer.set(-0.7);
      myDrive.tankDrive(0, 0);
    }else if(now-start < 9500){
      myDrive.tankDrive(0.7, 0.7);
    }else{
      myDrive.tankDrive(0, 0);
    }
    if( drawerOut.get() ) drawer.set( -0.7 ); //If the switch indicating that the drawer isn't all the way out isn't clicked, then make the drawer move out.
    else drawer.set( 0 ); //Else, stop moving.
