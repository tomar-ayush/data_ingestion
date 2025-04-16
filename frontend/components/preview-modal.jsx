"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import { ScrollArea } from "@/components/ui/scroll-area"

export default function PreviewModal({ data, onClose, onSelectColumns }) {
  const [selectedColumns, setSelectedColumns] = useState([])

  const handleColumnToggle = (column) => {
    setSelectedColumns((prev) => (prev.includes(column) ? prev.filter((col) => col !== column) : [...prev, column]))
  }

  const handleSelectAll = () => {
    if (selectedColumns.length === data.columns.length) {
      setSelectedColumns([])
    } else {
      setSelectedColumns([...data.columns])
    }
  }

  const handleConfirm = () => {
    onSelectColumns(selectedColumns)
  }

  if (!data || !data.columns || !data.rows) {
    return (
      <Dialog open onOpenChange={onClose}>
        <DialogContent className="sm:max-w-[800px]">
          <DialogHeader>
            <DialogTitle>No Data Available</DialogTitle>
          </DialogHeader>
          <div className="py-6 text-center">No data available to preview.</div>
          <DialogFooter>
            <Button onClick={onClose}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    )
  }

  return (
    <Dialog open onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[800px] max-h-[90vh]">
        <DialogHeader>
          <DialogTitle>Data Preview</DialogTitle>
        </DialogHeader>

        <div className="flex items-center gap-2 mb-2">
          <Checkbox
            id="select-all"
            checked={selectedColumns.length === data.columns.length && data.columns.length > 0}
            onCheckedChange={handleSelectAll}
          />
          <label htmlFor="select-all" className="text-sm font-medium">
            Select All Columns
          </label>
        </div>

        <ScrollArea className="h-[400px] rounded-md border">
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-muted">
                  <th className="p-2 border text-left">Select</th>
                  {data.columns.map((column) => (
                    <th key={column} className="p-2 border text-left">
                      {column}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {data.rows.map((row, index) => (
                  <tr key={index} className={index % 2 === 0 ? "bg-white" : "bg-gray-50"}>
                    <td className="p-2 border">
                      <div className="flex items-center justify-center">
                        {/* This is just a placeholder cell for the select column */}
                      </div>
                    </td>
                    {data.columns.map((column) => (
                      <td key={`${index}-${column}`} className="p-2 border">
                        {row[column] || ""}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </ScrollArea>

        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2 mt-4">
          {data.columns.map((column) => (
            <div key={column} className="flex items-center space-x-2">
              <Checkbox
                id={`column-${column}`}
                checked={selectedColumns.includes(column)}
                onCheckedChange={() => handleColumnToggle(column)}
              />
              <label
                htmlFor={`column-${column}`}
                className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
              >
                {column}
              </label>
            </div>
          ))}
        </div>

        <DialogFooter className="mt-4">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleConfirm} disabled={selectedColumns.length === 0}>
            Confirm Selection
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
