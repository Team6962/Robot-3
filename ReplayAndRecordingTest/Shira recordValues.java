public void recordValues(encoder1, encoder2, arraylist) {
  if ((now-start) % 50 == 0) {
    arraylist.add(new String[] {String.valueOf(encoder1.getDistance()), String.valueOf(encoder2.getDistance())});
  }
  if(joystick1.getRawButton(1)) {
    try {			
        FileWriter csvWriter = new FileWriter("test.csv");
        csvWriter.append("Links, AI Summary \n");
        
        for (int i = 0; i <arraylist.size(); i++) {
          for (int j = 0; j <arraylist.get(i).length; j++) {
            csvWriter.append(al.get(i)[j]);
            
            if (j !=arraylist.get(i).length-1) {
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
      System.out.println("DONE WRITING FILE.");
  }
}
