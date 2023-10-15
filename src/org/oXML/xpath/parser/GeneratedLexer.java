package org.oXML.xpath.parser;
import java_cup.runtime.Symbol;
import java.math.BigInteger;
import java.math.BigDecimal;


class GeneratedLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 65536;
	private final int YY_EOF = 65537;
	public final int YYEOF = -1;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	GeneratedLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	GeneratedLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private GeneratedLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private static int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NOT_ACCEPT,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NOT_ACCEPT,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NOT_ACCEPT,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR
	};
	private static final int yy_cmap[] = unpackFromString(1,65538,
"41:9,46:2,41,46:2,41:18,46,8,40,41,6,41:2,42,9,10,15,2,7,1,12,11,43:10,13,4" +
"1,3,5,4,41,14,44:26,16,41,17,41,45,41,33,36,25,21,22,35,31,37,30,44:2,34,26" +
",19,20,27,44,28,29,23,32,39,38,24,44:2,41,18,41:65411,0:2")[0];

	private static final int yy_rmap[] = unpackFromString(1,203,
"0,1:3,2,3,1:3,4,1:2,5,6,1:6,7,8,1:6,9,10,1:2,10:5,1,10,1:12,10,11:2,12,13,1" +
",14,15,13,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37" +
",38,39,40,41,42,43,44,45,46,47,48,49,50,51,10,52,53,54,55,56,57,58,59,60,61" +
",62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86" +
",87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108" +
",109,110,111,112,113,114,115,116,117,118,119,120,10,121,122,123,124,125,126" +
",127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,14" +
"5,146,147,148,149,150,151,152,153,154,155,156")[0];

	private static final int yy_nxt[][] = unpackFromString(157,47,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,54,91,166,178,166,183,92" +
",187,166,189,166:3,93,166,191,166:4,53,56,59,21,166:2,22,-1:52,23,-1:46,24," +
"-1:46,25,-1:52,26,-1:47,27,-1:30,28,-1:4,166,-1:17,166,94,166:12,95,166:6,-" +
"1:3,96,166,96,-1:13,28,-1:30,21,-1:46,28,-1:4,166,-1:17,166:21,-1:3,96,166," +
"96,-1:2,52:39,30,52:6,-1,166,-1:17,166:9,29,166:11,-1:3,96,166,96,-1:2,55:4" +
"1,31,55:4,-1,166,-1:17,166:20,32,-1:3,96,166,96,-1:14,37,-1:34,166,-1:17,16" +
"6:2,33,166:18,-1:3,96,166,96,-1:14,39,-1:34,166,-1:17,166:2,34,166:3,192,16" +
"6:14,-1:3,96,166,96,-1:14,40,-1:34,166,-1:17,166:3,35,166:17,-1:3,96,166,96" +
",-1:14,41,-1:34,166,-1:17,166:4,36,166:16,-1:3,96,166,96,-1:14,42,-1:34,166" +
",-1:11,58,-1:5,166:21,-1:3,96,166,96,-1:14,43,-1:34,166,-1:11,61,-1:5,166:2" +
"1,-1:3,96,166,96,-1:14,44,-1:34,166,-1:17,166:4,38,166:16,-1:3,96,166,96,-1" +
":14,45,-1:34,166,-1:11,63,-1:5,166:21,-1:3,96,166,96,-1:14,46,-1:34,139,-1:" +
"11,65,-1:5,166:21,-1:3,96,166,96,-1:14,47,-1:34,166,-1:11,67,-1:5,166:21,-1" +
":3,96,166,96,-1:14,48,-1:34,181,-1:11,69,-1:5,166:21,-1:3,96,166,96,-1:14,4" +
"9,-1:34,166,-1:11,71,-1:5,166:21,-1:3,96,166,96,-1:14,50,-1:34,202,-1:11,73" +
",-1:5,166:21,-1:3,96,166,96,-1:2,142,-1:11,75,-1:5,166:21,-1:3,96,166,96,-1" +
":2,166,-1:11,77,-1:5,166:21,-1:3,96,166,96,-1:2,166,-1:11,79,-1:5,166:21,-1" +
":3,96,166,96,-1:2,166,-1:11,81,-1:5,166:21,-1:3,96,166,96,-1:2,166,-1:11,83" +
",-1:5,166:21,-1:3,96,166,96,-1:2,166,-1:17,51,166:20,-1:3,96,166,96,-1:2,16" +
"6,-1:17,166:3,97,166:7,57,166:9,-1:3,96,166,96,-1:2,166,-1:17,166,60,166:19" +
",-1:3,96,166,96,-1:2,166,-1:17,62,166:3,103,166:16,-1:3,96,166,96,-1:2,166," +
"-1:17,166:2,64,166:18,-1:3,96,166,96,-1:2,166,-1:17,166:7,104,166:13,-1:3,9" +
"6,166,96,-1:2,166,-1:17,166:10,105,166:10,-1:3,96,166,96,-1:2,166,-1:17,166" +
":5,66,166:15,-1:3,96,166,96,-1:2,166,-1:17,166:11,179,166:9,-1:3,96,166,96," +
"-1:2,166,-1:17,166,193,166,197,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:9,1" +
"69,166:11,-1:3,96,166,96,-1:2,166,-1:17,166:15,107,166:5,-1:3,96,166,96,-1:" +
"2,166,-1:17,166:4,108,166:16,-1:3,96,166,96,-1:2,166,-1:17,166:3,110,166:17" +
",-1:3,96,166,96,-1:2,166,-1:17,166:6,111,166:14,-1:3,96,166,96,-1:2,166,-1:" +
"17,166:7,171,166:13,-1:3,96,166,96,-1:2,166,-1:17,166:16,68,166:4,-1:3,96,1" +
"66,96,-1:2,166,-1:17,166:9,114,166:11,-1:3,96,166,96,-1:2,166,-1:17,166:15," +
"115,166:5,-1:3,96,166,96,-1:2,166,-1:17,166:10,116,166:10,-1:3,96,166,96,-1" +
":2,166,-1:17,166:3,172,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:2,70,166:18" +
",-1:3,96,166,96,-1:2,166,-1:17,120,166:20,-1:3,96,166,96,-1:2,166,-1:17,166" +
":11,122,166:9,-1:3,96,166,96,-1:2,166,-1:17,166,123,166:19,-1:3,96,166,96,-" +
"1:2,166,-1:17,166:8,124,166:12,-1:3,96,166,96,-1:2,166,-1:17,72,166:20,-1:3" +
",96,166,96,-1:2,166,-1:17,166:10,173,166:10,-1:3,96,166,96,-1:2,166,-1:17,1" +
"66:2,125,166:18,-1:3,96,166,96,-1:2,166,-1:17,166:4,74,166:16,-1:3,96,166,9" +
"6,-1:2,166,-1:17,166:4,126,166:16,-1:3,96,166,96,-1:2,166,-1:17,166:17,127," +
"166:3,-1:3,96,166,96,-1:2,166,-1:17,166:19,194,166,-1:3,96,166,96,-1:2,166," +
"-1:17,166:14,128,166:6,-1:3,96,166,96,-1:2,166,-1:17,166:11,130,166:9,-1:3," +
"96,166,96,-1:2,166,-1:17,166,131,166:19,-1:3,96,166,96,-1:2,166,-1:17,166:1" +
"3,132,166:7,-1:3,96,166,96,-1:2,166,-1:17,166:6,133,166:14,-1:3,96,166,96,-" +
"1:2,166,-1:17,166:14,182,166:6,-1:3,96,166,96,-1:2,166,-1:17,134,166:20,-1:" +
"3,96,166,96,-1:2,166,-1:17,166:9,76,166:11,-1:3,96,166,96,-1:2,166,-1:17,16" +
"6:4,135,166:16,-1:3,96,166,96,-1:2,166,-1:17,166:3,78,166:17,-1:3,96,166,96" +
",-1:2,166,-1:17,166:12,80,166:8,-1:3,96,166,96,-1:2,166,-1:17,166:3,82,166:" +
"17,-1:3,96,166,96,-1:2,166,-1:17,166:12,84,166:8,-1:3,96,166,96,-1:2,166,-1" +
":17,166:4,85,166:16,-1:3,96,166,96,-1:2,166,-1:17,166:12,140,166:8,-1:3,96," +
"166,96,-1:2,166,-1:17,166,141,166:19,-1:3,96,166,96,-1:2,143,-1:17,166:21,-" +
"1:3,96,166,96,-1:2,166,-1:17,166:9,144,166:11,-1:3,96,166,96,-1:2,166,-1:17" +
",166,145,166:19,-1:3,96,166,96,-1:2,166,-1:17,166:11,146,166:9,-1:3,96,166," +
"96,-1:2,185,-1:17,166:21,-1:3,96,166,96,-1:2,166,-1:17,166:9,195,166:11,-1:" +
"3,96,166,96,-1:2,166,-1:17,188,166:20,-1:3,96,166,96,-1:2,166,-1:17,166:17," +
"148,166:3,-1:3,96,166,96,-1:2,166,-1:17,166:15,151,166:5,-1:3,96,166,96,-1:" +
"2,166,-1:17,166:3,152,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:4,154,166:16" +
",-1:3,96,166,96,-1:2,166,-1:17,166:11,155,166:9,-1:3,96,166,96,-1:2,166,-1:" +
"17,166:15,156,166:5,-1:3,96,166,96,-1:2,166,-1:17,166:3,157,166:17,-1:3,96," +
"166,96,-1:2,166,-1:17,166:9,158,166:11,-1:3,96,166,96,-1:2,166,-1:17,159,16" +
"6:20,-1:3,96,166,96,-1:2,166,-1:17,166:16,86,166:4,-1:3,96,166,96,-1:2,166," +
"-1:17,166:15,161,166:5,-1:3,96,166,96,-1:2,166,-1:17,166:13,162,166:7,-1:3," +
"96,166,96,-1:2,166,-1:17,166:12,87,166:8,-1:3,96,166,96,-1:2,166,-1:17,166:" +
"12,88,166:8,-1:3,96,166,96,-1:2,166,-1:17,166:16,89,166:4,-1:3,96,166,96,-1" +
":2,166,-1:17,166:6,163,166:14,-1:3,96,166,96,-1:2,166,-1:17,166:4,164,166:1" +
"6,-1:3,96,166,96,-1:2,166,-1:17,166:11,165,166:9,-1:3,96,166,96,-1:2,166,-1" +
":17,166,90,166:19,-1:3,96,166,96,-1:2,166,-1:17,166:7,106,166:13,-1:3,96,16" +
"6,96,-1:2,166,-1:17,166:15,109,166:5,-1:3,96,166,96,-1:2,166,-1:17,166:3,11" +
"3,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:10,121,166:10,-1:3,96,166,96,-1:" +
"2,166,-1:17,166:3,117,166:17,-1:3,96,166,96,-1:2,166,-1:17,174,166:20,-1:3," +
"96,166,96,-1:2,166,-1:17,166:10,198,166:10,-1:3,96,166,96,-1:2,166,-1:17,16" +
"6:2,129,166:18,-1:3,96,166,96,-1:2,166,-1:17,136,166:20,-1:3,96,166,96,-1:2" +
",166,-1:17,166:11,147,166:9,-1:3,96,166,96,-1:2,166,-1:17,160,166:20,-1:3,9" +
"6,166,96,-1:2,166,-1:17,166:3,98,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:1" +
"5,112,166:5,-1:3,96,166,96,-1:2,166,-1:17,166:3,118,166:17,-1:3,96,166,96,-" +
"1:2,166,-1:17,166:10,176,166:10,-1:3,96,166,96,-1:2,166,-1:17,137,166:20,-1" +
":3,96,166,96,-1:2,166,-1:17,166,167,166:16,99,166:2,-1:3,96,166,96,-1:2,166" +
",-1:17,166:3,119,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:10,149,166:10,-1:" +
"3,96,166,96,-1:2,166,-1:17,138,166:20,-1:3,96,166,96,-1:2,166,-1:17,166:9,1" +
"00,166:4,101,166:6,-1:3,96,166,96,-1:2,166,-1:17,166:10,150,166:10,-1:3,96," +
"166,96,-1:2,166,-1:17,166:3,102,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:10" +
",153,166:10,-1:3,96,166,96,-1:2,166,-1:17,166,168,166:19,-1:3,96,166,96,-1:" +
"2,166,-1:17,166:3,170,166:17,-1:3,96,166,96,-1:2,166,-1:17,166:6,180,166:14" +
",-1:3,96,166,96,-1:2,166,-1:17,166:11,175,166:9,-1:3,96,166,96,-1:2,190,-1:" +
"17,166:21,-1:3,96,166,96,-1:2,166,-1:17,166:11,177,166:9,-1:3,96,166,96,-1:" +
"2,166,-1:17,166:6,184,166:14,-1:3,96,166,96,-1:2,166,-1:17,166:11,186,166:9" +
",-1:3,96,166,96,-1:2,166,-1:17,166:15,196,166:5,-1:3,96,166,96,-1:2,166,-1:" +
"17,166:17,199,166:3,-1:3,96,166,96,-1:2,166,-1:17,166:11,200,166:9,-1:3,96," +
"166,96,-1:2,166,-1:17,166:10,201,166:10,-1:3,96,166,96,-1");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException, 
ParserException

		{
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(sym.MINUS); }
					case -3:
						break;
					case 3:
						{ return new Symbol(sym.PLUS); }
					case -4:
						break;
					case 4:
						{ return new Symbol(sym.LT); }
					case -5:
						break;
					case 5:
						{ return new Symbol(sym.GT); }
					case -6:
						break;
					case 6:
						{ return new Symbol(sym.EQUALS); }
					case -7:
						break;
					case 7:
						{ return new Symbol(sym.DOLLAR); }
					case -8:
						break;
					case 8:
						{ return new Symbol(sym.COMMA); }
					case -9:
						break;
					case 9:
						{ throw new ParserException("illegal character", yytext()); }
					case -10:
						break;
					case 10:
						{ return new Symbol(sym.LPAREN); }
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.RPAREN); }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.SLASH); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.DOT); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.COLON); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.AT); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.ASTERISK); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.LANGLE); }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.RANGLE); }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.BAR); }
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -21:
						break;
					case 21:
						{ return new Symbol(sym.NUMBER, new Double(yytext())); }
					case -22:
						break;
					case 22:
						{ /* ignore white space. */ }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.LTE); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.GTE); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.NOTEQUALS); }
					case -26:
						break;
					case 26:
						{ return new Symbol(sym.SLASHSLASH); }
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.DOTDOT); }
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.NUMBER, new Double(yytext())); }
					case -29:
						break;
					case 29:
						{ return new Symbol(sym.OR); }
					case -30:
						break;
					case 30:
						{ return new Symbol(sym.LITERAL, yytext().substring(1, yytext().length() - 1)); }
					case -31:
						break;
					case 31:
						{ return new Symbol(sym.LITERAL, yytext().substring(1, yytext().length() - 1)); }
					case -32:
						break;
					case 32:
						{ return new Symbol(sym.DIV); }
					case -33:
						break;
					case 33:
						{ return new Symbol(sym.MOD); }
					case -34:
						break;
					case 34:
						{ return new Symbol(sym.AND); }
					case -35:
						break;
					case 35:
						{ return new Symbol(sym.NODE); }
					case -36:
						break;
					case 36:
						{ return new Symbol(sym.TEXT); }
					case -37:
						break;
					case 37:
						{ return new Symbol(sym.SELF); }
					case -38:
						break;
					case 38:
						{ return new Symbol(sym.COMMENT); }
					case -39:
						break;
					case 39:
						{ return new Symbol(sym.CHILD); }
					case -40:
						break;
					case 40:
						{ return new Symbol(sym.PARENT); }
					case -41:
						break;
					case 41:
						{ return new Symbol(sym.ANCESTOR); }
					case -42:
						break;
					case 42:
						{ return new Symbol(sym.NAMESPACE); }
					case -43:
						break;
					case 43:
						{ return new Symbol(sym.PRECEDING); }
					case -44:
						break;
					case 44:
						{ return new Symbol(sym.ATTRIBUTE); }
					case -45:
						break;
					case 45:
						{ return new Symbol(sym.FOLLOWING); }
					case -46:
						break;
					case 46:
						{ return new Symbol(sym.DESCENDANT); }
					case -47:
						break;
					case 47:
						{ return new Symbol(sym.ANCESTOR_OR_SELF); }
					case -48:
						break;
					case 48:
						{ return new Symbol(sym.PRECEDING_SIBLING); }
					case -49:
						break;
					case 49:
						{ return new Symbol(sym.FOLLOWING_SIBLING); }
					case -50:
						break;
					case 50:
						{ return new Symbol(sym.DESCENDANT_OR_SELF); }
					case -51:
						break;
					case 51:
						{ return new Symbol(sym.PROCESSING_INSTRUCTION); }
					case -52:
						break;
					case 53:
						{ throw new ParserException("illegal character", yytext()); }
					case -53:
						break;
					case 54:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -54:
						break;
					case 56:
						{ throw new ParserException("illegal character", yytext()); }
					case -55:
						break;
					case 57:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -56:
						break;
					case 59:
						{ throw new ParserException("illegal character", yytext()); }
					case -57:
						break;
					case 60:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -58:
						break;
					case 62:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -59:
						break;
					case 64:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -60:
						break;
					case 66:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -61:
						break;
					case 68:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -62:
						break;
					case 70:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -63:
						break;
					case 72:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -64:
						break;
					case 74:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -65:
						break;
					case 76:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -66:
						break;
					case 78:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -67:
						break;
					case 80:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -68:
						break;
					case 82:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -69:
						break;
					case 84:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -70:
						break;
					case 85:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -71:
						break;
					case 86:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -72:
						break;
					case 87:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -73:
						break;
					case 88:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -74:
						break;
					case 89:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -75:
						break;
					case 90:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -76:
						break;
					case 91:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -77:
						break;
					case 92:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -78:
						break;
					case 93:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -79:
						break;
					case 94:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -80:
						break;
					case 95:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -81:
						break;
					case 96:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -82:
						break;
					case 97:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -83:
						break;
					case 98:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -84:
						break;
					case 99:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -85:
						break;
					case 100:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -86:
						break;
					case 101:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -87:
						break;
					case 102:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -88:
						break;
					case 103:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -89:
						break;
					case 104:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -90:
						break;
					case 105:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -91:
						break;
					case 106:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -92:
						break;
					case 107:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -93:
						break;
					case 108:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -94:
						break;
					case 109:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -95:
						break;
					case 110:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -96:
						break;
					case 111:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -97:
						break;
					case 112:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -98:
						break;
					case 113:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -99:
						break;
					case 114:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -100:
						break;
					case 115:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -101:
						break;
					case 116:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -102:
						break;
					case 117:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -103:
						break;
					case 118:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -104:
						break;
					case 119:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -105:
						break;
					case 120:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -106:
						break;
					case 121:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -107:
						break;
					case 122:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -108:
						break;
					case 123:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -109:
						break;
					case 124:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -110:
						break;
					case 125:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -111:
						break;
					case 126:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -112:
						break;
					case 127:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -113:
						break;
					case 128:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -114:
						break;
					case 129:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -115:
						break;
					case 130:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -116:
						break;
					case 131:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -117:
						break;
					case 132:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -118:
						break;
					case 133:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -119:
						break;
					case 134:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -120:
						break;
					case 135:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -121:
						break;
					case 136:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -122:
						break;
					case 137:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -123:
						break;
					case 138:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -124:
						break;
					case 139:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -125:
						break;
					case 140:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -126:
						break;
					case 141:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -127:
						break;
					case 142:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -128:
						break;
					case 143:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -129:
						break;
					case 144:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -130:
						break;
					case 145:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -131:
						break;
					case 146:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -132:
						break;
					case 147:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -133:
						break;
					case 148:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -134:
						break;
					case 149:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -135:
						break;
					case 150:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -136:
						break;
					case 151:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -137:
						break;
					case 152:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -138:
						break;
					case 153:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -139:
						break;
					case 154:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -140:
						break;
					case 155:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -141:
						break;
					case 156:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -142:
						break;
					case 157:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -143:
						break;
					case 158:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -144:
						break;
					case 159:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -145:
						break;
					case 160:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -146:
						break;
					case 161:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -147:
						break;
					case 162:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -148:
						break;
					case 163:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -149:
						break;
					case 164:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -150:
						break;
					case 165:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -151:
						break;
					case 166:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -152:
						break;
					case 167:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -153:
						break;
					case 168:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -154:
						break;
					case 169:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -155:
						break;
					case 170:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -156:
						break;
					case 171:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -157:
						break;
					case 172:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -158:
						break;
					case 173:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -159:
						break;
					case 174:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -160:
						break;
					case 175:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -161:
						break;
					case 176:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -162:
						break;
					case 177:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -163:
						break;
					case 178:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -164:
						break;
					case 179:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -165:
						break;
					case 180:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -166:
						break;
					case 181:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -167:
						break;
					case 182:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -168:
						break;
					case 183:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -169:
						break;
					case 184:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -170:
						break;
					case 185:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -171:
						break;
					case 186:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -172:
						break;
					case 187:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -173:
						break;
					case 188:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -174:
						break;
					case 189:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -175:
						break;
					case 190:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -176:
						break;
					case 191:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -177:
						break;
					case 192:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -178:
						break;
					case 193:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -179:
						break;
					case 194:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -180:
						break;
					case 195:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -181:
						break;
					case 196:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -182:
						break;
					case 197:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -183:
						break;
					case 198:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -184:
						break;
					case 199:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -185:
						break;
					case 200:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -186:
						break;
					case 201:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -187:
						break;
					case 202:
						{ return new Symbol(sym.NC_NAME, yytext()); }
					case -188:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
