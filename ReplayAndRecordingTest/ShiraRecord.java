public void autonomousPeriodic() {
  //Set a start time
  long start = System.currentTimeMillis();
  long now = System.currentTimeMillis();

  //Get starting encoder values (t=0)
  encoder1.reset();
  encoder2.reset();

  ArrayList<String[]]> encoderValues = new ArrayList<String[]>();
  //Set an interval to record at (every 0.05)
  while ((now-start) <= 60000) {
    long now = System.currentTimeMillis();
    if ((now-start) % 50 == 0) {
      encoderValues.add(new String[] {String.valueOf(encoder1.getDistance()), String.valueOf(encoder2.getDistance())});
    }
    long now = System.currentTimeMillis();
  }
  //Grab encoder at that time
  //ArrayList gets encoder
  //Arraylist is written to CSV file
  try {			
    FileWriter csvWriter = new FileWriter("\\Users\\mathi\Desktop\\test.csv");
    csvWriter.append("Links, AI Summary \n");
    
    for (int i = 0; i < encoderValues.size(); i++) {
      for (int j = 0; j < encoderValues.get(i).length; j++) {
        csvWriter.append(encoderValues.get(i)[j]);
        
        if (j != encoderValues.get(i).length-1) {
          csvWriter.append(", ");
        }
      }
      csvWriter.append("\n");
    }
    
    csvWriter.flush();
    csvWriter.close();
  } 
  catch (Exception e) {
    System.out.println(e + "ERROR IN WRITING FILE.");
  }
 }
