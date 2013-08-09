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
    private Double value;
    private Map<DecisionNode, Integer> results;
    private DecisionNode trueBranch;
    private DecisionNode falseBranch;

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Map<DecisionNode, Integer> getResults() {
        return results;
    }

    public void setResults(Map<DecisionNode, Integer> results) {
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
