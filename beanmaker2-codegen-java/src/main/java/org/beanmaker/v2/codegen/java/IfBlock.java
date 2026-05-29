package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class IfBlock extends LogicBranchBlock<IfBlock> {

    private List<ElseIfBlock> elseIfBlocks = new ArrayList<ElseIfBlock>();

    private ElseBlock elseBlock;


    public IfBlock(final Condition condition) {
        this(condition, 0);
    }

    public IfBlock(final Condition condition, final int indentationLevel) {
        super("if", indentationLevel, condition);
    }

    @Override
    protected IfBlock getThis() {
        return this;
    }

    @Override
    public void setIndentationLevel(final int indentationLevel) {
        super.setIndentationLevel(indentationLevel);
        for (ElseIfBlock elseIfBlock: elseIfBlocks)
            elseIfBlock.setIndentationLevel(indentationLevel);
        if (elseBlock != null)
            elseBlock.setIndentationLevel(indentationLevel);
    }


    public IfBlock addElseIfClause(final ElseIfBlock elseIfBlock) {
        if (!elseIfBlocks.isEmpty())
            elseIfBlocks.get(elseIfBlocks.size() - 1).moreElsesToCome();
        elseIfBlocks.add(elseIfBlock);
        elseIfBlock.setIndentationLevel(getIndentationLevel());
        return this;
    }

    public IfBlock elseClause(final ElseBlock elseBlock) {
        this.elseBlock = elseBlock;
        elseBlock.setIndentationLevel(getIndentationLevel());
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("if");
        appendCondition(buf);

        final boolean oneLiner = contentIsSuitableForOneLiner(!elseIfBlocks.isEmpty() || elseBlock != null);
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
                if (elseIfBlocks.get(elseIfBlocks.size() - 1).contentIsSuitableForOneLiner(true))
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
