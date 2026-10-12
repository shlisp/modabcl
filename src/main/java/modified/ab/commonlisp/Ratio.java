/*
 * Ratio.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id$
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package modified.ab.commonlisp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 *
 */
public class Ratio extends Number implements Formattable {

    private BigInteger numerator;
    private BigInteger denominator;

    public Ratio(BigInteger num, BigInteger deno) {
        if (deno.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }
        this.numerator = num;
        this.denominator = deno;
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    @Override
    public int intValue() {
        return numerator.divide(denominator).intValue();
    }

    @Override
    public long longValue() {
        return numerator.divide(denominator).longValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        // double の有効桁数（15〜17桁）に合わせてスケールを設定しています。
        return new BigDecimal(numerator)
                .divide(new BigDecimal(denominator), 17, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        // 1. 分数表記の文字列（例: "3/4" または "3"）を作成
        String text;
        if (denominator == null || denominator.equals(BigInteger.ONE)) {
            text = numerator != null ? numerator.toString() : "0";
        } else {
            text = numerator + "/" + denominator;
        }

        // 2. 精度 (precision) の処理: 指定がある場合は指定文字数で切り捨て
        if (precision != -1 && text.length() > precision) {
            text = text.substring(0, precision);
        }

        // 3. フラグの処理 (大文字変換)
        boolean uppercase = (flags & FormattableFlags.UPPERCASE) != 0;
        if (uppercase) {
            text = text.toUpperCase();
        }

        // 4. パディング (width) と整列 (LEFT_JUSTIFY) の処理
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) != 0;
        StringBuilder sb = new StringBuilder();
        int padding = width - text.length();

        if (padding > 0) {
            if (leftJustified) {
                sb.append(text);
                for (int i = 0; i < padding; i++) {
                    sb.append(' ');
                }
            } else {
                for (int i = 0; i < padding; i++) {
                    sb.append(' ');
                }
                sb.append(text);
            }
        } else {
            sb.append(text);
        }

        // 5. 結果を出力先に書き込み
        try {
            formatter.out().append(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
