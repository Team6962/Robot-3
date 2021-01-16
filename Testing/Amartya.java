if ( now - start < 1500 ) {
      myDrive.tankDrive( 0.8,0.8 );
 else if (now - start < 3500){
      myDrive.tankDrive( 0.7,0 );
      myDrive.tankDrive(0.7,-0.7);
 }
 else if (now - start < 4500){
      myDrive.tankDrive(0.4, 0.8);
    }
    else if (now - start < 6500){
      myDrive.tankDrive(0.8, 0.4);
    }
    else if (now - start < 8000){
      myDrive.tankDrive(0.8,-0.8);
    }
    else{
      myDrive.tankDrive(0,0);
    }
