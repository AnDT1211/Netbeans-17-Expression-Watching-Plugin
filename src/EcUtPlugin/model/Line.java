package EcUtPlugin.model;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private String content;
    private int idxStart;
    private int idxEnd;
    private int idxStartChar;

    private boolean hasComment;
    private int idxCodeEnd;
    private int idxCommentStart;
    private int codeLength;

    public Line(boolean hasComment, int idxCodeEnd, int idxCommentStart, int codeLength) {
        this.hasComment = hasComment;
        this.idxCodeEnd = idxCodeEnd;
        this.idxCommentStart = idxCommentStart;
        this.codeLength = codeLength;
    }

    public boolean isHasComment() {
        return hasComment;
    }

    public void setHasComment(boolean hasComment) {
        this.hasComment = hasComment;
    }

    public int getIdxCodeEnd() {
        return idxCodeEnd;
    }

    public void setIdxCodeEnd(int idxCodeEnd) {
        this.idxCodeEnd = idxCodeEnd;
    }

    public int getIdxCommentStart() {
        return idxCommentStart;
    }

    public void setIdxCommentStart(int idxCommentStart) {
        this.idxCommentStart = idxCommentStart;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public Line(String content, int idxStart, int idxEnd, int idxStartChar) {
        this.content = content;
        this.idxStart = idxStart;
        this.idxEnd = idxEnd;
        this.idxStartChar = idxStartChar;
    }

    public static List<Line> stringToLine(String text) {
        List<Line> lines = new ArrayList<>();
        String line = "";

        boolean isNewLineHaveChar = true;
        int fromIdx = 0;
        int i = 0;
        int idxStartChar = 0;
        for (; i < text.length(); i++) {
            line += text.charAt(i) + "";

            if (isNewLineHaveChar && text.charAt(i) != ' ') {
                idxStartChar = i;
                isNewLineHaveChar = false;
            }

            if (text.charAt(i) == '\n') {
                lines.add(new Line(line, fromIdx, i, idxStartChar));
                fromIdx = i + 1;
                line = "";
                isNewLineHaveChar = true;
            }
        }
        lines.add(new Line(line, fromIdx, i, idxStartChar));

        return lines;
    }

    @Override
    public String toString() {
        return "Line{" + "content=" + content + ", idxStart=" + idxStart + ", idxEnd=" + idxEnd + ", idxStartChar=" + idxStartChar + '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIdxStart() {
        return idxStart;
    }

    public void setIdxStart(int idxStart) {
        this.idxStart = idxStart;
    }

    public int getIdxEnd() {
        return idxEnd;
    }

    public void setIdxEnd(int idxEnd) {
        this.idxEnd = idxEnd;
    }

    public int getIdxStartChar() {
        return idxStartChar;
    }

    public void setIdxStartChar(int idxStartChar) {
        this.idxStartChar = idxStartChar;
    }
}
