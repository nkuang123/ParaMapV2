package main.java.backend;

import java.util.*;

/*
This class separates a list of sentences into their subjects and objects. It combines sentences with matching subjects
 */
public class SubPredSeparator{
    //Class Variables
    private HashMap<String, ArrayList<ArrayList<String>>> tokenizedSubPreds;
    private HashMap<String, ArrayList<String>> subPreds;
    private String head;
    private ArrayList<String> body;
    private Tagger tagger;
    private ArrayList<ArrayList<String>> listofListofSentences;
    private ArrayList<ArrayList<String>> listOfListOfTags;

    public SubPredSeparator() throws Exception{
        this.head = "";
        this.body = new ArrayList<>();
        this.tokenizedSubPreds = new HashMap<>();
        this.subPreds = new HashMap<>();
        this.tagger = new Tagger();
        this.listofListofSentences = new ArrayList<>();
        this.listOfListOfTags = new ArrayList<>();
    }

    //Creates map of head to body from a given list of sentences
    public void createMap(ArrayList<String> sentences) {
        //Adds sentences and tags to arrayLists
        createLists(sentences);

        //List of all possible subjects
        ArrayList<String> subjectList = new ArrayList<>(Arrays.asList("NN", "NNS", "NNP", "NNPS"));
        ArrayList<String> pluralList = new ArrayList<>(Arrays.asList("NNS", "NNPS"));
        //iterate through ArrayList of ArrayList of listOfListOfTags
        for (int i = 0; i < this.listOfListOfTags.size(); i++) {
            //ith ArrayList of Tags to iterate through
            ArrayList<String> listOfTags = this.listOfListOfTags.get(i);
            //Corresponding ith ArrayList of Sentences to extract head and body from
            ArrayList<String> listOfSentences = this.listofListofSentences.get(i);

            //Iterate through ith ArrayList of Tags
            for (int j = 0; j < listOfTags.size(); j++) {
                //jth tag to inspect
                String comparedTag = listOfTags.get(j);

                //Check if the tag is a subject tag
                if (subjectList.contains(comparedTag)) {
                    if(pluralList.contains(comparedTag)){
                        //if plurar, convert to singular first
                        this.head = convertToSingular(listOfSentences.get(j));
                    }
                    else {
                        this.head = listOfSentences.get(j);
                    }
                    //iterate through rest of sentence and add to body
                    for (int k = j+1; k < listOfSentences.size(); k++) {
                        this.body.add(listOfSentences.get(k));
                    }
                    break;
                }
            }
            //Adds the current head and body to the map. Checks if head already exists. If it does, it merges
            // it with new body
            addToMap();
            //Clean slate for next iteration
            clear();
        }

        createSubPreds();
    }

    //Adds sentences and tags to arrayLists
    private void createLists(ArrayList<String> sentences){
        for(String s : sentences){
            this.tagger.tagSentence(s);
            ArrayList<String> sent = new ArrayList<>(this.tagger.getSentence());
            ArrayList<String> tagz = new ArrayList<>(this.tagger.getTags());
            this.listofListofSentences.add(sent);
            this.listOfListOfTags.add(tagz);
            this.tagger.clear();
        }
    }


    //Adds the current head and body to the map. Checks if head already exists. If it does, it merges it with new body
    private void addToMap() {
        String head = this.head.toLowerCase();
        ArrayList<String> newbie = new ArrayList<>(this.body);
        if (this.tokenizedSubPreds.containsKey(head))
            if(this.tokenizedSubPreds.get(head).contains(newbie)){
                return;
            }
            else {
                this.tokenizedSubPreds.get(head).add(newbie);
            }
        else {
            this.tokenizedSubPreds.put(head, new ArrayList<>());
            this.tokenizedSubPreds.get(head).add(newbie);
        }
    }
    //Allows class reusability
    private void clear(){
        this.head = "";
        this.body.clear();
    }

    //Converts common plurals to singular
    private String convertToSingular(String word) {
        String singular = "";
        if (word.charAt(word.length() - 1) == 's') {
            if (word.charAt(word.length() - 2) == 'e') {
                if (word.charAt(word.length() - 3) == 'i') {
                    singular = word.substring(0, word.length() - 3) + "y";
                } else {
                    singular = word.substring(0, word.length() - 2);
                }
            }
            else {
                singular = word.substring(0, word.length()-1);
            }
        }
        else if ((word.charAt(word.length() - 1) == 'i')) {
            singular = word.substring(0, word.length()-1) + "us";
        }
        return singular;

    }
    //Concatenates the tokenized body of a sentence
    private void createSubPreds() {
        String body = "";
        for(String head : this.tokenizedSubPreds.keySet()) {
            ArrayList<String> concatenatedBody = new ArrayList<>();
            for (ArrayList<String> bods : this.tokenizedSubPreds.get(head)) {
                for(String s : bods) {
                    body += s + " ";
                }
                concatenatedBody.add(body.trim().replaceAll("\\s*\\p{Punct}+\\s*$", ""));
                body = "";
            }
            this.subPreds.put(head, concatenatedBody);
        }

        //splitPredsByConjunctions();
    }

    /**
     * Returns the map with tokenized bodies
     * @return HashMap<>
     */
    public HashMap<String, ArrayList<ArrayList<String>>> getTokenizedSubPreds() {
        return this.tokenizedSubPreds;
    }

    /**
     * Returns the map with concatenated bodies
     * @return HashMap<>
     */
    public HashMap<String, ArrayList<String>> getSubPreds() {
        return this.subPreds;
    }

    /**
     * Incomplete function that's supposed to split a string by conjuctions
     */

    private void splitPredsByConjunctions() {
        for (String head : this.subPreds.keySet()) {
            ArrayList<String> splitBodyElements = this.subPreds.get(head);
            for (String sentence : this.subPreds.get(head)) {
                String[] splitSentences = sentence.split("\\band\\b");
                if (splitSentences.length > 1) {
                    splitBodyElements.addAll(Arrays.asList(splitSentences));
                    splitBodyElements.remove(sentence);
                }
            }

            //this.subPreds.put(head, splitBodyElements);
        }

    }

    /**
     * printMap prints the contents of a HashMap, displaying the objects each subject maps to
     *
     */
    public void printMap(){
        String body = "";
        for(String head : this.tokenizedSubPreds.keySet()) {
            System.out.println("Head of sentence is: " + head);
            for (ArrayList<String> bods : this.tokenizedSubPreds.get(head)) {
                for(String s : bods) {
                    body += s + " ";
                }
                System.out.println("Body of sentence is: " +body);
                body = "";
            }
            System.out.println();
        }
    }


    public static void main(String[] args) throws Exception {
        String sentence = "is blue and is round.";
        String[] splitSentences = sentence.split("\\band\\b");
        for (int i = 0; i < splitSentences.length; i++) {
            System.out.println(splitSentences[i].trim());
        }
    }

}


