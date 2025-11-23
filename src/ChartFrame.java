import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;

public class ChartFrame extends JFrame {

    public ChartFrame(String title, String[] labels, double[] values, boolean barChart) {
        super(title);
        setSize(760, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new ChartPanel(labels, values, barChart));
    }

    private static class ChartPanel extends JPanel {
        private final String[] labels;
        private final double[] values;
        private final boolean barChart;

        public ChartPanel(String[] labels, double[] values, boolean barChart) {
            this.labels = labels;
            this.values = values;
            this.barChart = barChart;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw((Graphics2D) g);
        }

        private void draw(Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background
            g2.setColor(new Color(245, 247, 250));
            g2.fillRect(0, 0, w, h);

            double max = Arrays.stream(values).max().orElse(1);
            if (max == 0) max = 1;

            int margin = 60;
            int chartW = w - margin * 2;
            int chartH = h - margin * 2;

            // X-axis
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(margin, h - margin, w - margin, h - margin);

            if (barChart)
                drawBars(g2, margin, chartW, chartH, h, max);
            else
                drawLine(g2, margin, chartW, chartH, h, max);

            drawLabels(g2, margin, chartW, h);
        }

        private void drawBars(Graphics2D g2, int margin, int chartW, int chartH, int h, double max) {
            int n = values.length;
            if (n == 0) return;

            int barWidth = chartW / Math.max(1, n);

            for (int i = 0; i < n; i++) {
                double val = values[i];
                int barHeight = (int)((val / max) * chartH);

                int x = margin + i * barWidth + 10;
                int y = (h - margin) - barHeight;

                GradientPaint gp = new GradientPaint(
                        x, y, new Color(70, 130, 180),
                        x, y + barHeight, new Color(100, 160, 230)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barWidth - 20, barHeight, 14, 14);

                g2.setColor(Color.BLACK);
                g2.drawString(String.format("%.1f", val), x + 5, y - 8);
            }
        }

        private void drawLine(Graphics2D g2, int margin, int chartW, int chartH, int h, double max) {
            int n = values.length;

            if (n == 0) return;

            // Case 1: Only 1 point → draw a dot only
            if (n == 1) {
                int x = margin + chartW / 2;
                int y = (int) (h - margin - (values[0] / max) * chartH);
                g2.setColor(new Color(70, 130, 180));
                g2.fill(new Ellipse2D.Float(x - 6, y - 6, 12, 12));
                return;
            }

            // Normal case
            int step = chartW / (n - 1);

            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(70, 130, 180));

            for (int i = 0; i < n - 1; i++) {
                int x1 = margin + i * step;
                int x2 = margin + (i + 1) * step;

                int y1 = (int)(h - margin - (values[i] / max) * chartH);
                int y2 = (int)(h - margin - (values[i + 1] / max) * chartH);

                g2.draw(new Line2D.Float(x1, y1, x2, y2));
            }

            // Points
            g2.setColor(Color.RED);
            for (int i = 0; i < n; i++) {
                int x = margin + i * step;
                int y = (int)(h - margin - (values[i] / max) * chartH);
                g2.fill(new Ellipse2D.Float(x - 5, y - 5, 10, 10));
            }
        }

        private void drawLabels(Graphics2D g2, int margin, int chartW, int h) {
            int n = labels.length;
            if (n == 0) return;

            int step = chartW / Math.max(1, n);

            g2.setColor(Color.BLACK);
            for (int i = 0; i < n; i++) {
                int x = margin + i * step + 10;
                int y = h - margin + 20;
                g2.drawString(labels[i], x, y);
            }
        }
    }
}
