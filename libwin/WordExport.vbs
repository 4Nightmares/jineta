' ******************************
' * VB-Script Macro FrameWork  *
' * to export a recipe to      *
' * Microsoft Word             *
' * (c) 2005 Frank Tusche      *
' ******************************

Option Explicit
Dim wordApp, doc, mi, a, k, s, b, i, u

Sub setFormats()
  Dim f
  Set f = mi.FormattedText
  With f
    .Bold = b
    .Italic = i
    .Underline = u
  End With
  Set f = Nothing
  mi.Start = mi.End
End Sub

Sub InsertFormattedText(Text)
  mi.InsertAfter Text
  setFormats
End Sub

Sub setBookmarkText(Bookmark, Text)
  Dim yes
  b = False: i = False: u = False
  if Doc.Bookmarks.Exists(Bookmark) then
    Set mi = doc.ActiveWindow.Selection
    a = Doc.Bookmarks.Item(Bookmark).Range.Start
    mi.Start = a
    mi.End = Doc.Bookmarks.Item(Bookmark).Range.End
    s = Text
    If (InStr(s, "<L>") <= 0) And (InStr(s, "<F>") <= 0) And (InStr(s, "<K>") <= 0) And (InStr(s, "<U>") <= 0) And (InStr(s, "<N>") <= 0) Then
      mi.Text = s
    Else
      mi.Text = ""
      Do While Len(s) > 0
        k = InStr(s, "<")
        if k > 1 Then
          InsertFormattedText Left(s, k-1)
          s = Mid(s, k)
        ElseIf k <= 0 Then
          InsertFormattedText s
          s = ""
        End If
        yes = True
        If ((Len(s) > 2) And (Mid(s, 1, 1) = "<") And (Mid(s, 3, 1) = ">")) Or ((Len(s) > 3) And (Mid(s, 1, 1) = "<") And (Mid(s, 4, 1) = ">")) Then
          If Len(s) > 2 Then
            If (Mid(s, 1, 1) = "<") Then
              If (Mid(s, 3, 1) = ">") And (InStr("FKULN", Mid(s, 2, 1)) > 0) Then
                Select Case Mid(s, 2, 1)
                  Case "F" b = true
                  Case "K" i = true
                  Case "U" u = true
                  Case "L" mi.FormattedText.ListFormat.ApplyBulletDefault
                  Case "N" mi.FormattedText.ListFormat.ApplyNumberDefault
                End Select
                s = Mid(s, 4)
                yes = False
              End If
            End If
          End If
          If yes And Len(s) > 3 Then
            If (Mid(s, 1, 1) = "<") Then
              If (Mid(s, 2, 1) = "/") And (InStr("FKULN", Mid(s, 3, 1))) And (Mid(s, 4, 1) = ">") Then
                setFormats
                Select Case Mid(s, 3, 1)
                  Case "F" b = false
                  Case "K" i = false
                  Case "U" u = false
                  Case "L" mi.FormattedText.ListFormat.ApplyBulletDefault
                  Case "N" mi.FormattedText.ListFormat.ApplyNumberDefault
                End Select
                s = Mid(s, 5)
              Else
                InsertFormattedText Left(s, 1)
                s = Mid(s, 2)
              End If
            End If
          End If
        ElseIf Len(s) > 0 Then
          InsertFormattedText Left(s, 1)
          s = Mid(s, 2)
        End If
      Loop
    End If
    mi.Start = a
    Doc.Bookmarks.Add Bookmark, mi
    mi.Start = mi.End
  End If
  Set mi = Nothing
End Sub

Set wordApp = CreateObject("Word.Application")
wordApp.visible = true
' *********************************************************************
' *** Commands to open Template and set Bookmarks are appended here ***
' *********************************************************************
'Set doc = wordApp.Documents.add("WordTemplate.dot")
'setBookmarkText "name", "T<F>e</F><K>s</K><U>t</U><F><K><U>Eintrag</F></K></U>"

