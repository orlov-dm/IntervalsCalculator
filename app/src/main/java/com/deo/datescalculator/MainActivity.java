package com.deo.datescalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final int[] buttonIDs = {
            R.id.bZero,
            R.id.bOne,
            R.id.bTwo,
            R.id.bThree,
            R.id.bFour,
            R.id.bFive,
            R.id.bSix,
            R.id.bSeven,
            R.id.bEight,
            R.id.bNine
    };
    private final Character OP_ADD = '+';
    private final Character OP_SUB = '-';
    private final Character OP_DOT = '.';

    private String m_equation;

    private TreeMap<Integer, Integer> m_valuesMap = new TreeMap<>();
    private TreeMap<Integer, Character> m_operationsMap = new TreeMap<>();
    /*private TreeMap<Integer, Button> m_buttonsMap = new TreeMap<>();*/
    private TextView m_displayView;

    private TextView m_testView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();*/

        findViewById(R.id.bNotUsed1).setVisibility(View.GONE);
        findViewById(R.id.bNotUsed2).setVisibility(View.GONE);
        findViewById(R.id.textViewTest).setVisibility(View.GONE);

        m_equation = "";
        m_displayView = (TextView) findViewById(R.id.textView);
        m_testView = (TextView) findViewById(R.id.textViewTest);
        updateDisplay();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer id = v.getId();
                buttonPress(id);
            }
        };

        int initValue = 0;
        for (int buttonID : buttonIDs) {
            m_valuesMap.put(buttonID, initValue++);

            Button b = (Button) findViewById(buttonID);
            b.setOnClickListener(listener);
//            m_buttonsMap.put(buttonID, b);
        }
        findViewById(R.id.bAdd).setOnClickListener(listener);
        findViewById(R.id.bSubtract).setOnClickListener(listener);
        findViewById(R.id.bDot).setOnClickListener(listener);
        findViewById(R.id.bCancel).setOnClickListener(listener);
        findViewById(R.id.bBackspace).setOnClickListener(listener);
        findViewById(R.id.bEnter).setOnClickListener(listener);

        m_operationsMap.put(R.id.bAdd, OP_ADD);
        m_operationsMap.put(R.id.bSubtract, OP_SUB);
        m_operationsMap.put(R.id.bDot, OP_DOT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buttonPress(int id) {
        String equation = m_equation;

        try {
            if (m_valuesMap.containsKey(id)) {
                Integer value = m_valuesMap.get(id);
                equation += String.valueOf(value);
                //values
            } else if (m_operationsMap.containsKey(id)) {
                //operations
                Character op = m_operationsMap.get(id);
                Character last = equation.isEmpty()?null:equation.charAt(equation.length() - 1);
                if(!equation.isEmpty() && !isOperation(last) || equation.isEmpty() && op == OP_SUB) {
                    equation += op;
                }
            } else if (id == R.id.bBackspace) {
                if(!equation.isEmpty()) {
                    equation = equation.substring(0, equation.length() - 1);
                }
            } else if (id == R.id.bCancel) {
                //cancel
                equation = "";
            } else if (id == R.id.bEnter) {
                //calc
                m_testView.setText("Enter");

                if(!equation.isEmpty()) {
                    Pattern p = Pattern.compile("(\\d+(?:\\.\\d+){0,2})(\\+|-)?");
                    Matcher match = p.matcher(equation);
                    Character op = null;
                    Integer totalDays = 0;
                    Integer totalYears = 0;
                    Integer totalMonths = 0;
                    boolean isError = false;
                    if(equation.startsWith("-")) {
                        op = OP_SUB;
                    }
                    while (match.find()) {
                        String group = match.group(1);
                        String[] results = group.split("\\.");
                        if(results.length > 0) {
                            int years, months, days;
                            years = months = 0;
                            if(results.length == 3) {
                                years = Integer.valueOf(results[0]);
                                months = Integer.valueOf(results[1]);
                                days = Integer.valueOf(results[2]);
                            } else if (results.length == 2) {
                                months = Integer.valueOf(results[0]);
                                days = Integer.valueOf(results[1]);
                            } else {
                                days = Integer.valueOf(results[0]);
                            }

                            //int value = years * 356 + months * 30 + days;
                            if(op != null && op == OP_SUB) {
                                years *= -1;
                                months *= -1;
                                days *= -1;
                            }

                            totalYears += years;
                            totalMonths += months;
                            totalDays += days;

                            if(match.group(2) != null && !match.group(2).isEmpty()) {
                                op = match.group(2).charAt(0);
                            } else {
                                op = null;
                            }
                        } else {
                            equation = getString(R.string.error);
                            isError = true;
                            break;
                        }
                        m_testView.setText( m_testView.getText() + "\n" + match.group() + " / " + match.group(1) + " / " + match.group(2));
                    }

                    if(!isError) {

                        if(Math.abs(totalDays) >= 30) {
                            int m = totalDays / 30;
                            totalMonths += m;
                            totalDays -= m * 30;
                        }

                        if (Math.abs(totalMonths) >= 12) {
                            int y = totalMonths / 12;
                            totalYears += y;
                            totalMonths -= y * 12;
                        }

                        m_testView.setText( m_testView.getText() + "\n" + String.valueOf(totalYears) + '.' + String.valueOf(totalMonths) + '.' + String.valueOf(totalDays));

                        boolean isNegative = totalYears < 0 || totalYears == 0 && totalMonths < 0 || totalYears == 0 && totalMonths == 0 && totalDays < 0;

                        if (isNegative) {
                            totalYears *= -1;
                            totalMonths *= -1;
                            totalDays *= -1;
                        }




                        m_testView.setText( m_testView.getText() + "\n" + String.valueOf(totalYears) + '.' + String.valueOf(totalMonths) + '.' + String.valueOf(totalDays));


                        if (totalYears > 0) {
                            if (totalMonths < 0) {
                                int y = (int)Math.floor(totalMonths / 12.0)*-1;
                                totalYears -= y;
                                totalMonths += y * 12;
                            }
                        }

                        if (totalMonths > 0) {
                            if (totalDays < 0) {
                                int m = (int)Math.floor(totalDays / 30.0)*-1;
                                totalMonths -= m;
                                totalDays += m * 30;
                            }
                        }


                        if (!isNegative) {
                            equation = "";
                        } else {
                            equation = "-";
                        }
                        equation += String.valueOf(totalYears) + '.' + String.valueOf(totalMonths) + '.' + String.valueOf(totalDays);
                    }
                }
            }
            m_equation = equation;
            updateDisplay();
        } catch (Exception ignored) {

        }
    }

    private boolean isOperation(Character value) {
        return m_operationsMap.values().contains(value);
    }

    private void updateDisplay() {
        m_displayView.setText(m_equation);
    }
}
