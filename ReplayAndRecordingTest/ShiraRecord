public void autonomousPeriodic() {
  //Set a start time
  long start = System.currentTimeMillis();
  long now = System.currentTimeMillis();

  //Get starting encoder values (t=0)
  encoder1.reset();
  encoder2.reset();

  ArrayList<double[]> encoderValues = new ArrayList<double[]>();
  //Set an interval to record at (every 0.05)
  if ((now-start) % 50 == 0) {
    encoderValues.add(new double[] {encoder1.getDistance(), encoder2.getDistance()});
  }
  //Grab encoder at that time
  //ArrayList gets encoder
  //Arraylist is written to CSV file
  //IDK how to write to a CSV file
 }
