package digital.slovensko.autogram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import digital.slovensko.autogram.core.SigningJob;
import digital.slovensko.autogram.core.visualization.DocumentVisualizationBuilder;
import digital.slovensko.autogram.core.visualization.HTMLVisualization;
import digital.slovensko.autogram.server.dto.Document;
import digital.slovensko.autogram.server.dto.ServerSigningParameters;
import digital.slovensko.autogram.server.dto.SignRequestBody;
import digital.slovensko.autogram.server.errors.RequestValidationException;
import eu.europa.esig.dss.enumerations.SignatureLevel;

public class SigningJobTests {

        private static final String transformation = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjx4c2w6c3R5bGVzaGVldCB2ZXJzaW9uPSIxLjAiICB4bWxuczp4c2w9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvWFNML1RyYW5zZm9ybSIgIHhtbG5zOmVnb25wPSJodHRwOi8vc2NoZW1hcy5nb3Yuc2svZm9ybS9BcHAuR2VuZXJhbEFnZW5kYS8xLjkiIGV4Y2x1ZGUtcmVzdWx0LXByZWZpeGVzPSJlZ29ucCI+DQo8eHNsOm91dHB1dCBtZXRob2Q9Imh0bWwiIGRvY3R5cGUtc3lzdGVtPSJodHRwOi8vd3d3LnczLm9yZy9UUi9odG1sNC9zdHJpY3QuZHRkIiBkb2N0eXBlLXB1YmxpYz0iLS8vVzNDLy9EVEQgSFRNTCA0LjAxLy9FTiIgaW5kZW50PSJubyIgb21pdC14bWwtZGVjbGFyYXRpb249InllcyIvPg0KPHhzbDp0ZW1wbGF0ZSBtYXRjaD0iLyI+DQo8aHRtbD4NCjxoZWFkPg0KPG1ldGEgaHR0cC1lcXVpdj0iWC1VQS1Db21wYXRpYmxlIiBjb250ZW50PSJJRT04IiAvPg0KPHRpdGxlPlbFoWVvYmVjbsOhIGFnZW5kYTwvdGl0bGU+DQo8bWV0YSBodHRwLWVxdWl2PSJDb250ZW50LVR5cGUiIGNvbnRlbnQ9InRleHQvaHRtbDsgY2hhcnNldD1VVEYtOCIvPg0KPG1ldGEgbmFtZT0ibGFuZ3VhZ2UiIGNvbnRlbnQ9InNrLVNLIi8+DQo8c3R5bGUgdHlwZT0idGV4dC9jc3MiPg0KYm9keSB7IA0KCWZvbnQtZmFtaWx5OiAnT3BlbiBTYW5zJywgJ1NlZ29lIFVJJywgJ1RyZWJ1Y2hldCBNUycsICdHZW5ldmEgQ0UnLCBsdWNpZGEsIHNhbnMtc2VyaWY7DQoJYmFja2dyb3VuZCA6ICNmZmZmZmYgIWltcG9ydGFudCA7DQp9DQoudWktdGFicyB7DQoJcGFkZGluZzogLjJlbTsNCglwb3NpdGlvbjogcmVsYXRpdmU7DQoJem9vbTogMTsNCn0JCQkJCQkJCQ0KLmNsZWFyIHsgY2xlYXI6IGJvdGg7IGhlaWdodDogMDt9DQoubGF5b3V0TWFpbiB7DQoJbWFyZ2luOiAwcHggYXV0bzsNCglwYWRkaW5nOiA1cHggNXB4IDVweCA1cHg7CQ0KfQkJCQkNCi5sYXlvdXRSb3cgeyBtYXJnaW4tYm90dG9tOiA1cHg7IH0JCQkJDQouY2FwdGlvbiB7IC8qd2lkdGg6IDEwMCU7IGJvcmRlci1ib3R0b206IHNvbGlkIDFweCBibGFjazsqLyB9DQoubm9jYXB0aW9uICZndDsgLmNhcHRpb24geyBib3JkZXI6IDBweCAhaW1wb3J0YW50OyB9DQoubm9jYXB0aW9uICZndDsgLmNhcHRpb24gc3BhbiB7DQoJYmFja2dyb3VuZDogbm9uZSAhaW1wb3J0YW50Ow0KCWRpc3BsYXk6IG5vbmU7DQp9IA0KLmNhcHRpb24gLnRpdGxlIHsgcGFkZGluZy1sZWZ0OiA1cHg7IH0NCi5oZWFkZXJjb3JyZWN0aW9uIHsJDQoJbWFyZ2luOiAwcHg7DQogICAgZm9udC1zaXplIDogMWVtOw0KICAgIGZvbnQtd2VpZ2h0OiBib2xkOw0KfQkJCQkNCi5sYWJlbFZpcyB7DQoJZmxvYXQ6IGxlZnQ7DQoJZm9udC13ZWlnaHQ6IGJvbGQ7DQoJZm9udC1mYW1pbHk6ICdPcGVuIFNhbnMnLCAnU2Vnb2UgVUknLCAnVHJlYnVjaGV0IE1TJywgJ0dlbmV2YSBDRScsIGx1Y2lkYSwgc2Fucy1zZXJpZjsNCglsaW5lLWhlaWdodDogMjVweDsNCgltYXJnaW46IDBweCAxOHB4IDBweCAwcHg7DQoJcGFkZGluZy1sZWZ0OiAzcHg7DQoJd2lkdGg6IDE5MHB4Ow0KCXdvcmQtd3JhcDogYnJlYWstd29yZDsNCiAgICBmb250LXNpemU6IDAuOGVtOw0KfQ0KLmNvbnRlbnRWaXMgeyAgICAJICAgICANCglmbG9hdDogbGVmdDsJDQoJbGluZS1oZWlnaHQ6IDI1cHg7DQoJbWFyZ2luOiAwcHg7DQoJcGFkZGluZzogMHB4Ow0KCXZlcnRpY2FsLWFsaWduOiB0b3A7DQogICAgZm9udC1zaXplOiAwLjc1ZW07CQkJDQp9DQoud29yZHdyYXAgeyANCiAgICB3aGl0ZS1zcGFjZTogcHJlLXdyYXA7ICAgICAgDQogICAgd2hpdGUtc3BhY2U6IC1tb3otcHJlLXdyYXA7IA0KICAgIHdoaXRlLXNwYWNlOiAtcHJlLXdyYXA7ICAgICANCiAgICB3aGl0ZS1zcGFjZTogLW8tcHJlLXdyYXA7ICAgDQogICAgd29yZC13cmFwOiBicmVhay13b3JkOyAgICAgIA0KfQkNCi51aS13aWRnZXQtY29udGVudCB7DQoJYmFja2dyb3VuZCA6IDUwJSA1MCUgcmVwZWF0LXggI2ZmZmZmZjsNCglib3JkZXIgOiAjZDRkNGQ0IHNvbGlkIDJweDsNCgljb2xvciA6ICM0ZjRlNGU7DQoJYm9yZGVyLXJhZGl1cyA6IDNweDsNCn0NCi51aS13aWRnZXQtaGVhZGVyIHsNCgljdXJzb3IgOiBwb2ludGVyOw0KCWZvbnQtc2l6ZSA6IDAuOGVtOw0KCWNvbG9yIDogIzQ5NDk0OTsNCglwYWRkaW5nLWxlZnQgOiAycHg7DQoJYm9yZGVyIDogI2VhZTllOCBzb2xpZCAxcHg7DQoJYmFja2dyb3VuZC1jb2xvciA6ICNlYWU5ZTg7DQoJbWFyZ2luLWJvdHRvbTogM3B4Ow0KCWJvcmRlci1yYWRpdXMgOiAzcHg7DQp9CQkJDQo8L3N0eWxlPg0KPC9oZWFkPg0KPGJvZHk+DQo8ZGl2IGlkPSJtYWluIiBjbGFzcz0ibGF5b3V0TWFpbiI+DQo8eHNsOmFwcGx5LXRlbXBsYXRlcy8+DQo8L2Rpdj4NCjwvYm9keT4NCjwvaHRtbD4NCjwveHNsOnRlbXBsYXRlPg0KPHhzbDp0ZW1wbGF0ZSBtYXRjaD0iL2Vnb25wOkdlbmVyYWxBZ2VuZGEiPg0KPGRpdiBjbGFzcz0ibGF5b3V0Um93IHVpLXRhYnMgdWktd2lkZ2V0LWNvbnRlbnQiID4NCjxkaXYgY2xhc3M9ImNhcHRpb24gdWktd2lkZ2V0LWhlYWRlciI+DQo8ZGl2IGNsYXNzPSJoZWFkZXJjb3JyZWN0aW9uIj5WxaFlb2JlY27DoSBhZ2VuZGE8L2Rpdj4NCjwvZGl2Pg0KPHhzbDphcHBseS10ZW1wbGF0ZXMgc2VsZWN0PSIuL2Vnb25wOnN1YmplY3QiLz4NCjx4c2w6YXBwbHktdGVtcGxhdGVzIHNlbGVjdD0iLi9lZ29ucDp0ZXh0Ii8+DQo8L2Rpdj4NCjwveHNsOnRlbXBsYXRlPg0KPHhzbDp0ZW1wbGF0ZSBtYXRjaD0iZWdvbnA6R2VuZXJhbEFnZW5kYS9lZ29ucDpzdWJqZWN0Ij4NCjx4c2w6aWYgdGVzdD0iLi90ZXh0KCkiPg0KPGRpdj48bGFiZWwgY2xhc3M9ImxhYmVsVmlzIj5QcmVkbWV0OiA8L2xhYmVsPjxzcGFuIGNsYXNzPSJjb250ZW50VmlzIHdvcmR3cmFwIj48eHNsOmNhbGwtdGVtcGxhdGUgbmFtZT0ic3RyaW5nLXJlcGxhY2UtYWxsIj48eHNsOndpdGgtcGFyYW0gbmFtZT0idGV4dCIgc2VsZWN0PSIuIiAvPjx4c2w6d2l0aC1wYXJhbSBuYW1lPSJyZXBsYWNlIiBzZWxlY3Q9IiclMEEnIiAvPjx4c2w6d2l0aC1wYXJhbSBuYW1lPSJieSIgc2VsZWN0PSInJiMxMzsmIzEwOyciIC8+PC94c2w6Y2FsbC10ZW1wbGF0ZT48L3NwYW4+PC9kaXY+PGRpdiBjbGFzcz0iY2xlYXIiPiYjeGEwOzwvZGl2PjwveHNsOmlmPg0KPC94c2w6dGVtcGxhdGU+DQo8eHNsOnRlbXBsYXRlIG1hdGNoPSJlZ29ucDpHZW5lcmFsQWdlbmRhL2Vnb25wOnRleHQiPg0KPHhzbDppZiB0ZXN0PSIuL3RleHQoKSI+DQo8ZGl2PjxsYWJlbCBjbGFzcz0ibGFiZWxWaXMiPlRleHQ6IDwvbGFiZWw+PHNwYW4gY2xhc3M9ImNvbnRlbnRWaXMgd29yZHdyYXAiPjx4c2w6Y2FsbC10ZW1wbGF0ZSBuYW1lPSJzdHJpbmctcmVwbGFjZS1hbGwiPjx4c2w6d2l0aC1wYXJhbSBuYW1lPSJ0ZXh0IiBzZWxlY3Q9Ii4iIC8+PHhzbDp3aXRoLXBhcmFtIG5hbWU9InJlcGxhY2UiIHNlbGVjdD0iJyUwQSciIC8+PHhzbDp3aXRoLXBhcmFtIG5hbWU9ImJ5IiBzZWxlY3Q9IicmIzEzOyYjMTA7JyIgLz48L3hzbDpjYWxsLXRlbXBsYXRlPjwvc3Bhbj48L2Rpdj48ZGl2IGNsYXNzPSJjbGVhciI+JiN4YTA7PC9kaXY+PC94c2w6aWY+DQo8L3hzbDp0ZW1wbGF0ZT4NCjx4c2w6dGVtcGxhdGUgbmFtZT0iZm9ybWF0VG9Ta0RhdGUiPg0KPHhzbDpwYXJhbSBuYW1lPSJkYXRlIiAvPg0KPHhzbDp2YXJpYWJsZSBuYW1lPSJkYXRlU3RyaW5nIiBzZWxlY3Q9InN0cmluZygkZGF0ZSkiIC8+DQo8eHNsOmNob29zZT4NCjx4c2w6d2hlbiB0ZXN0PSIkZGF0ZVN0cmluZyAhPSAnJyBhbmQgc3RyaW5nLWxlbmd0aCgkZGF0ZVN0cmluZyk9MTAgYW5kIHN0cmluZyhudW1iZXIoc3Vic3RyaW5nKCRkYXRlU3RyaW5nLCAxLCA0KSkpICE9ICdOYU4nICI+DQo8eHNsOnZhbHVlLW9mIHNlbGVjdD0iY29uY2F0KHN1YnN0cmluZygkZGF0ZVN0cmluZywgOSwgMiksICcuJywgc3Vic3RyaW5nKCRkYXRlU3RyaW5nLCA2LCAyKSwgJy4nLCBzdWJzdHJpbmcoJGRhdGVTdHJpbmcsIDEsIDQpKSIgLz4NCjwveHNsOndoZW4+DQo8eHNsOm90aGVyd2lzZT4NCjx4c2w6dmFsdWUtb2Ygc2VsZWN0PSIkZGF0ZVN0cmluZyI+PC94c2w6dmFsdWUtb2Y+DQo8L3hzbDpvdGhlcndpc2U+DQo8L3hzbDpjaG9vc2U+DQo8L3hzbDp0ZW1wbGF0ZT4NCjx4c2w6dGVtcGxhdGUgbmFtZT0iYm9vbGVhbkNoZWNrYm94VG9TdHJpbmciPg0KPHhzbDpwYXJhbSBuYW1lPSJib29sVmFsdWUiIC8+DQo8eHNsOnZhcmlhYmxlIG5hbWU9ImJvb2xWYWx1ZVN0cmluZyIgc2VsZWN0PSJzdHJpbmcoJGJvb2xWYWx1ZSkiIC8+DQo8eHNsOmNob29zZT4NCjx4c2w6d2hlbiB0ZXN0PSIkYm9vbFZhbHVlU3RyaW5nID0gJ3RydWUnICI+DQo8eHNsOnRleHQ+w4FubzwveHNsOnRleHQ+DQo8L3hzbDp3aGVuPg0KPHhzbDp3aGVuIHRlc3Q9IiRib29sVmFsdWVTdHJpbmcgPSAnZmFsc2UnICI+DQo8eHNsOnRleHQ+TmllPC94c2w6dGV4dD4NCjwveHNsOndoZW4+DQo8eHNsOndoZW4gdGVzdD0iJGJvb2xWYWx1ZVN0cmluZyA9ICcxJyAiPg0KPHhzbDp0ZXh0PsOBbm88L3hzbDp0ZXh0Pg0KPC94c2w6d2hlbj4NCjx4c2w6d2hlbiB0ZXN0PSIkYm9vbFZhbHVlU3RyaW5nID0gJzAnICI+DQo8eHNsOnRleHQ+TmllPC94c2w6dGV4dD4NCjwveHNsOndoZW4+DQo8eHNsOm90aGVyd2lzZT4NCjx4c2w6dmFsdWUtb2Ygc2VsZWN0PSIkYm9vbFZhbHVlU3RyaW5nIj48L3hzbDp2YWx1ZS1vZj4NCjwveHNsOm90aGVyd2lzZT4NCjwveHNsOmNob29zZT4NCjwveHNsOnRlbXBsYXRlPg0KPHhzbDp0ZW1wbGF0ZSBuYW1lPSJmb3JtYXRUaW1lVHJpbVNlY29uZHMiPg0KPHhzbDpwYXJhbSBuYW1lPSJ0aW1lIiAvPg0KPHhzbDp2YXJpYWJsZSBuYW1lPSJ0aW1lU3RyaW5nIiBzZWxlY3Q9InN0cmluZygkdGltZSkiIC8+DQo8eHNsOmlmIHRlc3Q9IiR0aW1lU3RyaW5nICE9ICcnIj4NCjx4c2w6dmFsdWUtb2Ygc2VsZWN0PSJzdWJzdHJpbmcoJHRpbWVTdHJpbmcsIDEsIDUpIiAvPg0KPC94c2w6aWY+DQo8L3hzbDp0ZW1wbGF0ZT4NCjx4c2w6dGVtcGxhdGUgbmFtZT0iZm9ybWF0VGltZSI+DQo8eHNsOnBhcmFtIG5hbWU9InRpbWUiIC8+DQo8eHNsOnZhcmlhYmxlIG5hbWU9InRpbWVTdHJpbmciIHNlbGVjdD0ic3RyaW5nKCR0aW1lKSIgLz4NCjx4c2w6aWYgdGVzdD0iJHRpbWVTdHJpbmcgIT0gJyciPg0KPHhzbDp2YWx1ZS1vZiBzZWxlY3Q9InN1YnN0cmluZygkdGltZVN0cmluZywgMSwgOCkiIC8+DQo8L3hzbDppZj4NCjwveHNsOnRlbXBsYXRlPg0KPHhzbDp0ZW1wbGF0ZSBuYW1lPSJzdHJpbmctcmVwbGFjZS1hbGwiPg0KPHhzbDpwYXJhbSBuYW1lPSJ0ZXh0Ii8+DQo8eHNsOnBhcmFtIG5hbWU9InJlcGxhY2UiLz4NCjx4c2w6cGFyYW0gbmFtZT0iYnkiLz4NCjx4c2w6Y2hvb3NlPg0KPHhzbDp3aGVuIHRlc3Q9ImNvbnRhaW5zKCR0ZXh0LCAkcmVwbGFjZSkiPg0KPHhzbDp2YWx1ZS1vZiBzZWxlY3Q9InN1YnN0cmluZy1iZWZvcmUoJHRleHQsJHJlcGxhY2UpIi8+DQo8eHNsOnZhbHVlLW9mIHNlbGVjdD0iJGJ5Ii8+DQo8eHNsOmNhbGwtdGVtcGxhdGUgbmFtZT0ic3RyaW5nLXJlcGxhY2UtYWxsIj4NCjx4c2w6d2l0aC1wYXJhbSBuYW1lPSJ0ZXh0IiBzZWxlY3Q9InN1YnN0cmluZy1hZnRlcigkdGV4dCwkcmVwbGFjZSkiLz4NCjx4c2w6d2l0aC1wYXJhbSBuYW1lPSJyZXBsYWNlIiBzZWxlY3Q9IiRyZXBsYWNlIi8+DQo8eHNsOndpdGgtcGFyYW0gbmFtZT0iYnkiIHNlbGVjdD0iJGJ5IiAvPg0KPC94c2w6Y2FsbC10ZW1wbGF0ZT4NCjwveHNsOndoZW4+DQo8eHNsOm90aGVyd2lzZT4NCjx4c2w6dmFsdWUtb2Ygc2VsZWN0PSIkdGV4dCIvPg0KPC94c2w6b3RoZXJ3aXNlPg0KPC94c2w6Y2hvb3NlPg0KPC94c2w6dGVtcGxhdGU+DQo8eHNsOnRlbXBsYXRlIG5hbWU9ImZvcm1hdFRvU2tEYXRlVGltZSI+DQo8eHNsOnBhcmFtIG5hbWU9ImRhdGVUaW1lIiAvPg0KPHhzbDp2YXJpYWJsZSBuYW1lPSJkYXRlVGltZVN0cmluZyIgc2VsZWN0PSJzdHJpbmcoJGRhdGVUaW1lKSIgLz4NCjx4c2w6Y2hvb3NlPg0KPHhzbDp3aGVuIHRlc3Q9IiRkYXRlVGltZVN0cmluZyE9ICcnIGFuZCBzdHJpbmctbGVuZ3RoKCRkYXRlVGltZVN0cmluZyk+MTggYW5kIHN0cmluZyhudW1iZXIoc3Vic3RyaW5nKCRkYXRlVGltZVN0cmluZywgMSwgNCkpKSAhPSAnTmFOJyAiPg0KPHhzbDp2YWx1ZS1vZiBzZWxlY3Q9ImNvbmNhdChzdWJzdHJpbmcoJGRhdGVUaW1lU3RyaW5nLCA5LCAyKSwgJy4nLCBzdWJzdHJpbmcoJGRhdGVUaW1lU3RyaW5nLCA2LCAyKSwgJy4nLCBzdWJzdHJpbmcoJGRhdGVUaW1lU3RyaW5nLCAxLCA0KSwnICcsIHN1YnN0cmluZygkZGF0ZVRpbWVTdHJpbmcsIDEyLCAyKSwnOicsIHN1YnN0cmluZygkZGF0ZVRpbWVTdHJpbmcsIDE1LCAyKSkiIC8+DQo8L3hzbDp3aGVuPg0KPHhzbDpvdGhlcndpc2U+DQo8eHNsOnZhbHVlLW9mIHNlbGVjdD0iJGRhdGVUaW1lU3RyaW5nIj48L3hzbDp2YWx1ZS1vZj4NCjwveHNsOm90aGVyd2lzZT4NCjwveHNsOmNob29zZT4NCjwveHNsOnRlbXBsYXRlPg0KPHhzbDp0ZW1wbGF0ZSBuYW1lPSJmb3JtYXRUb1NrRGF0ZVRpbWVTZWNvbmQiPg0KPHhzbDpwYXJhbSBuYW1lPSJkYXRlVGltZSIgLz4NCjx4c2w6dmFyaWFibGUgbmFtZT0iZGF0ZVRpbWVTdHJpbmciIHNlbGVjdD0ic3RyaW5nKCRkYXRlVGltZSkiIC8+DQo8eHNsOmNob29zZT4NCjx4c2w6d2hlbiB0ZXN0PSIkZGF0ZVRpbWVTdHJpbmchPSAnJyBhbmQgc3RyaW5nLWxlbmd0aCgkZGF0ZVRpbWVTdHJpbmcpPjE4IGFuZCBzdHJpbmcobnVtYmVyKHN1YnN0cmluZygkZGF0ZVRpbWVTdHJpbmcsIDEsIDQpKSkgIT0gJ05hTicgIj4NCjx4c2w6dmFsdWUtb2Ygc2VsZWN0PSJjb25jYXQoc3Vic3RyaW5nKCRkYXRlVGltZVN0cmluZywgOSwgMiksICcuJywgc3Vic3RyaW5nKCRkYXRlVGltZVN0cmluZywgNiwgMiksICcuJywgc3Vic3RyaW5nKCRkYXRlVGltZVN0cmluZywgMSwgNCksJyAnLCBzdWJzdHJpbmcoJGRhdGVUaW1lU3RyaW5nLCAxMiwgMiksJzonLCBzdWJzdHJpbmcoJGRhdGVUaW1lU3RyaW5nLCAxNSwgMiksJzonLCBzdWJzdHJpbmcoJGRhdGVUaW1lU3RyaW5nLCAxOCwgMikpIiAvPg0KPC94c2w6d2hlbj4NCjx4c2w6b3RoZXJ3aXNlPg0KPHhzbDp2YWx1ZS1vZiBzZWxlY3Q9IiRkYXRlVGltZVN0cmluZyI+PC94c2w6dmFsdWUtb2Y+DQo8L3hzbDpvdGhlcndpc2U+DQo8L3hzbDpjaG9vc2U+DQo8L3hzbDp0ZW1wbGF0ZT4NCjwveHNsOnN0eWxlc2hlZXQ+DQoNCg==";

        private static final String content = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48R2VuZXJhbEFnZW5kYSB4bWxucz0iaHR0cDovL3NjaGVtYXMuZ292LnNrL2Zvcm0vQXBwLkdlbmVyYWxBZ2VuZGEvMS45Ij4KICA8c3ViamVjdD5Ob3bDqSBwb2RhbmllPC9zdWJqZWN0PgogIDx0ZXh0PlBvZMOhdmFtIHRvdG8gbm92w6kgcG9kYW5pZS48L3RleHQ+CjwvR2VuZXJhbEFnZW5kYT4=";

        @Test
        void testEnd2EndHtmlTransformationEncoding() throws IOException, RequestValidationException,
                        ParserConfigurationException, SAXException, TransformerException {
                var ssParams = new ServerSigningParameters(
                                SignatureLevel.XAdES_BASELINE_B,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                transformation,
                                null,
                                false,
                                ServerSigningParameters.VisualizationWidthEnum.sm);

                var signRequestBody = new SignRequestBody(new Document(content), ssParams, "application/xml;base64");
                var job = new SigningJob(signRequestBody.getDocument(), signRequestBody.getParameters(), null);
                var visualizedDocument = DocumentVisualizationBuilder.fromJob(job).build();
                if (visualizedDocument instanceof HTMLVisualization d) {
                        var htmlTransformed = d.getDocument();
                        var expected = new String(
                                        this.getClass().getResourceAsStream("transformed.html").readAllBytes(),
                                        StandardCharsets.UTF_8);

                        assertEquals(expected.replaceAll("\\r\\n?", "\n"),
                                        htmlTransformed.replaceAll("\\r\\n?", "\n"));
                } else {
                        fail("Not an HTMLVisualizedDocument");
                }
        }
}
