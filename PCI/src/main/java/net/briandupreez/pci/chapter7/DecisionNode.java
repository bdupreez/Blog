package net.briandupreez.pci.chapter7;

import java.util.Map;

/**
 * DecisionNode.
 * User: bdupreez
 * Date: 2013/08/09
 * Time: 7:15 AM
 */
public class DecisionNode {

    private Integer col;
    private Object value;
    private Map<String, Double> results;
    private DecisionNode trueBranch;
    private DecisionNode falseBranch;

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, Double> getResults() {
        return results;
    }

    public void setResults(Map<String, Double> results) {
        this.results = results;
    }

    public DecisionNode getTrueBranch() {
        return trueBranch;
    }

    public void setTrueBranch(DecisionNode trueBranch) {
        this.trueBranch = trueBranch;
    }

    public DecisionNode getFalseBranch() {
        return falseBranch;
    }

    public void setFalseBranch(DecisionNode falseBranch) {
        this.falseBranch = falseBranch;
    }
}
