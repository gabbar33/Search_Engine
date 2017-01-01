package lucene;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * Simple command-line based search demo.
 */
public class SearchFiles extends JFrame implements ActionListener {

    JTextField t;
    JButton b;
    JTextArea ta;
    String index, field, queries, queryString;
    int repeat;
    boolean raw;
    JScrollPane scroll;
    //JPanel jp;

    public SearchFiles() {
        super("Search Engine");
        setLayout(null);
        t = new JTextField();
        t.setBounds(130, 40, 200, 40);
        add(t);
        b = new JButton("Submit");
        b.setBounds(180, 90, 100, 30);
        add(b);
        
        //jp=new JPanel();
        //jp.setBackground(Color.red);
        //jp.setSize(500,500);
        ta = new JTextArea();
        ta.setEditable(false);
        ta.setBounds(10, 140, 460, 600);
        ta.setSize(500,500);
        ta.setLineWrap(true);
        ta.setVisible(true);
        ta.setWrapStyleWord(true);
        add(ta);
        
        
        setSize(550, 800);
        setVisible(true);
        b.addActionListener(this);

    }

    /**
     * Simple command-line based search demo.
     */
    public static void main(String[] args) {
        SearchFiles obj = new SearchFiles();
        String usage =
                "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
          //  System.out.println(usage);
            //obj.ta.setText(usage);
            obj.ta.append(usage+"\n");
            System.exit(0);
        }

    }

    void fun() throws Exception {


        String index = "C:\\Users\\brajendra\\Desktop\\lucene tutorials\\Lucene-Tutorials-master\\index";
        String field = "contents";
        String queries = null;
        int repeat = 0;
        boolean raw = false;

        // regular search
        String queryString = t.getText().trim();

        //wildcard query
        //String queryString = "t*t";

        //fuzzy query
//    String queryString = "roam~2";

        //phrase query 
//    String queryString = "\"apache lucene\"~5";

        //boolean search
//    String queryString = "\"networks\" AND \"protocol\"";


        //boosted search
        //String queryString = "computer^10 crime";
        // System.out.println("birju");
        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        BufferedReader in = null;
        QueryParser parser = new QueryParser(field, analyzer);

        Query query = parser.parse(queryString);

        //System.out.println("Searching for: " + query.toString(field));
        ta.append("Searching for: " + query.toString(field)+"\n");
        searcher.search(query, null, 100);
        doSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);
        reader.close();
    }

    /**
     * This demonstrates a typical paging search scenario, where the search
     * engine presents pages of size n to the user. The user can then go to the
     * next page if interested in the next hits.
     *
     * When the query is executed for the first time, then only enough results
     * are collected to fill 5 result pages. If the user wants to page beyond
     * this limit, then the query is executed another time and all hits are
     * collected.
     *
     */
    public void doSearch(BufferedReader in, IndexSearcher searcher, Query query,
            int hitsPerPage, boolean raw, boolean interactive) throws IOException {

        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        //System.out.println(numTotalHits + " total matching documents");
        ta.setText(numTotalHits + " total matching documents\n");
        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        for (int i = start; i < end; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get("path");
            if (path != null) {
                //System.out.println((i + 1) + ". " + path);
                ta.append((i + 1) + " " + path+"\n");
                String title = doc.get("title");
                if (title != null) {
                 //   System.out.println("   Title: " + doc.get("title"));
                    ta.append("   Title:" + doc.get("title")+"\n");
                }
            } else {
               // System.out.println((i + 1) + ". " + "No path for this document");
                ta.append((i + 1) + ". " + "No path for this document");
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            fun();
        } catch (Exception ex) {
            Logger.getLogger(SearchFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
