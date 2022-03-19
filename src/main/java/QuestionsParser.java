import java.io.*;
import java.util.ArrayList;

public class QuestionsParser {

    ArrayList<String> words = new ArrayList<>();

    QuestionsParser() {
        try {
            File file = new File("./src/main/resources/ResultWords.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            for (int i = 0; i < 7827; i++) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    words.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> createResultWords() {
        ArrayList<String> resultWords = new ArrayList<>();
        for(int i = 0; i < 10; i ++) {
            int j = (int)(Math.random()*7827);
            String resultEn = words.get(j).replaceAll("[а-я]","")
                    .replaceAll(",","")
                    .replaceAll(" ","")
                    .replaceAll(";","");
            String resultRus = words.get(j).replaceAll("[a-z]","");
            resultWords.add(resultEn);
            resultWords.add(resultRus);
        }
        return resultWords;
    }
}
