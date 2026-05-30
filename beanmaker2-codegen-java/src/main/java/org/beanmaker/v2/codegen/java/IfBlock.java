package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class IfBlock extends LogicBranchBlock<IfBlock> {

    private final List<ElseIfBlock> elseIfBlocks = new ArrayList<>();

    private ElseBlock elseBlock;


    public IfBlock(Condition condition) {
        this(condition, 0);
    }

    public IfBlock(Condition condition, int indentationLevel) {
        super("if", indentationLevel, condition);
    }

    @Override
    protected IfBlock getThis() {
        return this;
    }

    @Override
    public void setIndentationLevel(int indentationLevel) {
        super.setIndentationLevel(indentationLevel);
        for (ElseIfBlock elseIfBlock: elseIfBlocks)
            elseIfBlock.setIndentationLevel(indentationLevel);
        if (elseBlock != null)
            elseBlock.setIndentationLevel(indentationLevel);
    }


    public IfBlock addElseIfClause(ElseIfBlock elseIfBlock) {
        if (!elseIfBlocks.isEmpty())
            elseIfBlocks.getLast().moreElsesToCome();
        elseIfBlocks.add(elseIfBlock);
        elseIfBlock.setIndentationLevel(getIndentationLevel());
        return this;
    }

    public IfBlock elseClause(ElseBlock elseBlock) {
        this.elseBlock = elseBlock;
        elseBlock.setIndentationLevel(getIndentationLevel());
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("if");
        appendCondition(buf);

        boolean oneLiner = contentIsSuitableForOneLiner(!elseIfBlocks.isEmpty() || elseBlock != null);
        if (oneLiner) {
            appendOneLinerContent(buf);
        } else {
            buf.append(" ");
            appendContent(buf);
        }

        if (!elseIfBlocks.isEmpty()) {
            boolean first = true;
            int index = 0;
            for (ElseIfBlock elseIfBlock: elseIfBlocks) {
                if (first) {
                    first = false;
                    if (oneLiner)
                        elseIfBlock.atStartOfLine();
                } else {
                    if (elseIfBlocks.get(index - 1).contentIsSuitableForOneLiner(elseBlock != null || (index + 1) < elseIfBlocks.size()))
                        elseIfBlock.atStartOfLine();
                }
                ++index;
                buf.append(elseIfBlock.toString());
            }
        }

        if (elseBlock != null) {
            if (elseIfBlocks.isEmpty()) {
                if (oneLiner)
                    elseBlock.atStartOfLine();
            } else {
                if (elseIfBlocks.getLast().contentIsSuitableForOneLiner(true))
                    elseBlock.atStartOfLine();
            }
            buf.append(elseBlock.toString());
        }

        String ifBlock = buf.toString();
        if (ifBlock.endsWith("\n"))
            return ifBlock;

        return ifBlock + "\n";
    }

}
